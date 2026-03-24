import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import java.net.InetAddress;

import java.nio.file.Paths;

public class FileDownloader {
    private final P2P p2p;
    private final NetworkCommunicator communicator;
    private final ReentrantLock confirmLock;
    private final ReentrantLock downloadLock;
    private final ArrayList<SmallInformation> confirmIDControlList;
    private final ArrayList<byte[]> downloadIDControlList;
    private InetAddress address;
    private final DynamicBuffer dynamicBuffer;

    public FileDownloader(P2P p2p, NetworkCommunicator communicator,
            ReentrantLock confirmLock, ReentrantLock downloadLock,
            ArrayList<SmallInformation> confirmIDControlList,
            ArrayList<byte[]> downloadIDControlList) {
        this.p2p = p2p;
        this.communicator = communicator;
        this.confirmLock = confirmLock;
        this.downloadLock = downloadLock;
        this.confirmIDControlList = confirmIDControlList;
        this.downloadIDControlList = downloadIDControlList;
        this.dynamicBuffer = new DynamicBuffer();
    }

    public void download(String bufferFolder, int id, FileInfo fileInfo)
            throws IOException, InterruptedException {
        String filename = fileInfo.filename;
        long bytes = fileInfo.bytes;
        String hash = fileInfo.hash;

        long downloadedBytes = 0;
        int chunkNum = 1;

        if (!createFile(bufferFolder, filename)) {
            showDownloadError();
            return;
        }

        String fullPath = Paths.get(bufferFolder, filename).toString();
        FileOutputStream fos = new FileOutputStream(fullPath);

        boolean videoStarted = false;

        while (downloadedBytes != bytes) {
            byte[] result = downloadChunk(id, downloadedBytes, hash);
            if (result == null) {
                continue;
            }

            fos.write(result, 0, result.length);
            fos.flush();
            downloadedBytes += result.length;

            double progress = (1.0 * downloadedBytes) / bytes;
            p2p.setVideoInfo(address, chunkNum, id, progress);

            if (!videoStarted && downloadedBytes >= Constants.MIN_BYTES_TO_START) {
                videoStarted = true;
                final String videoPath = fullPath;
                new Thread(() -> {
                    try {
                        p2p.playVideo(videoPath, filename);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            if (videoStarted) {
                p2p.onNewChunkDownloaded();
            }

            if (downloadedBytes == -1) {
                fos.close();
                return;
            }

            clearDownloadBuffer(id);

            chunkNum++;
        }

        fos.close();
        p2p.onDownloadComplete();
    }

    private byte[] downloadChunk(int id, long downloadedBytes, String hash)
            throws IOException, InterruptedException {
        resetConfirmBuffer(id);

        if (!communicator.isRunning()) {
            throw new IOException();
        }

        long startTime = System.currentTimeMillis();
        String controlMsg = MessageParser.buildControlMessage(downloadedBytes, id, hash);
        communicator.sendBroadcast(Constants.MSG_CONTROL + controlMsg);

        SmallInformation si = waitForConfirm(id);
        if (si == null) {
            dynamicBuffer.recordPacketLoss();
            return null;
        }
        dynamicBuffer.recordLatency(System.currentTimeMillis() - startTime);

        if (!communicator.isRunning()) {
            throw new IOException();
        }

        address = si.getAddress();

        String requestMsg = MessageParser.buildRequestMessage(downloadedBytes, si.getNum());
        communicator.sendPrivate(Constants.MSG_REQUEST + requestMsg, address);

        byte[] buffer = waitForDownload(id);
        if (buffer == null) {
            dynamicBuffer.recordPacketLoss();
            return null;
        }
        dynamicBuffer.recordPacketSuccess();

        return buffer;
    }

    private SmallInformation waitForConfirm(int id) throws InterruptedException {
        for (int attempt = 0; attempt < Constants.CONFIRM_RETRY_COUNT; attempt++) {
            confirmLock.lock();
            SmallInformation si = confirmIDControlList.get(id);
            confirmLock.unlock();

            if (si != null) {
                return si;
            }

            Thread.sleep(Constants.RETRY_SLEEP_MS);
        }
        return null;
    }

    private byte[] waitForDownload(int id) throws InterruptedException {
        for (int attempt = 0; attempt < Constants.REQUEST_RETRY_COUNT; attempt++) {
            downloadLock.lock();
            byte[] buffer = downloadIDControlList.get(id);
            downloadLock.unlock();

            if (buffer != null) {
                return buffer;
            }

            Thread.sleep(Constants.RETRY_SLEEP_MS);
        }
        return null;
    }

    private void resetConfirmBuffer(int id) {
        confirmLock.lock();
        confirmIDControlList.set(id, null);
        confirmLock.unlock();
    }

    private void clearDownloadBuffer(int id) {
        downloadLock.lock();
        downloadIDControlList.set(id, null);
        downloadLock.unlock();
    }

    private boolean createFile(String basePath, String filename) {
        try {
            File file = new File(basePath, filename);
            if (file.exists()) {
                return true;
            }
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private void showDownloadError() {
        JOptionPane.showMessageDialog(p2p, "An error occurred while downloading!",
                "Download Cancelled", JOptionPane.ERROR_MESSAGE);
    }

    public DynamicBuffer getDynamicBuffer() {
        return dynamicBuffer;
    }
}
