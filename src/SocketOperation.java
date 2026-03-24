import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.security.NoSuchAlgorithmException;

import javax.swing.DefaultListModel;

import com.google.gson.Gson;

import java.nio.file.Path;

public class SocketOperation {
    public static Gson gson = new Gson();
    public static DefaultListModel<String> excludedFoldersList;

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ReentrantLock uploadLock = new ReentrantLock();
    private final ReentrantLock runningLock = new ReentrantLock();
    private final ReentrantLock confirmLock = new ReentrantLock();
    private final ReentrantLock downloadLock = new ReentrantLock();
    private final ReentrantLock shareFolderLock = new ReentrantLock();

    private DatagramSocket udpReceiverSocket;
    private ServerSocket tcpReceiverSocket;
    private byte[] buffer;
    private DatagramPacket packet;

    private P2P p2p;
    private int uploadThreadNum = 0;

    private NetworkCommunicator communicator;
    private FileDownloader downloader;
    private FileUploader uploader;

    public ArrayList<Long> uploadIDControlList = new ArrayList<>();
    public ArrayList<SmallInformation> confirmIDControlList = new ArrayList<>();
    public ArrayList<byte[]> downloadIDControlList = new ArrayList<>();

    public SocketOperation(P2P p2p) throws UnknownHostException {
        this.p2p = p2p;
        this.communicator = new NetworkCommunicator(runningLock);
        this.downloader = new FileDownloader(p2p, communicator, confirmLock, downloadLock,
                confirmIDControlList, downloadIDControlList);
        this.uploader = new FileUploader(uploadLock, uploadIDControlList, communicator);
        communicator.setRunning(false);
        updateBufferSize();
    }

    public void connect() {
        communicator.setRunning(true);
        threadPool.execute(this::shareFolder);
        threadPool.execute(this::udpReceiver);
        threadPool.execute(this::tcpReceiver);
    }

    public void disconnect() {
        try {
            communicator.sendBroadcast(Constants.MSG_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
        }

        communicator.setRunning(false);
        communicator.close();

        if (udpReceiverSocket != null) {
            udpReceiverSocket.close();
        }

        try {
            if (tcpReceiverSocket != null) {
                tcpReceiverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addIDControlList() {
        confirmLock.lock();
        confirmIDControlList.add(null);
        confirmLock.unlock();

        downloadLock.lock();
        downloadIDControlList.add(null);
        downloadLock.unlock();
    }

    public void download(String bufferFolder, int id, FileInfo fileInfo)
            throws IOException, InterruptedException {
        downloader.download(bufferFolder, id, fileInfo);
    }

    private void shareFolder() {
        try {
            communicator.initialize();

            while (communicator.isRunning()) {
                shareFolderLock.lock();
                try {
                    String sharedFolder = p2p.getSharedFolder();

                    if (!sharedFolder.equals("")) {
                        broadcastSharedFiles(sharedFolder);
                    } else {
                        communicator.sendBroadcast(Constants.MSG_FOUND);
                    }
                } finally {
                    shareFolderLock.unlock();
                }
                sleepWithRunningCheck(Constants.SHARE_FOLDER_INTERVAL_MS);
            }
        } catch (IOException | InterruptedException e) {
            releaseLocks();
            if (!e.getMessage().equals("Socket closed")) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastSharedFiles(String sharedFolder) throws IOException {
        File folder = new File(sharedFolder);
        Path baseFolderPath = folder.toPath();
        List<File> files = FolderOperation.getAllFiles(folder);

        StringBuilder jsonsPart = new StringBuilder();

        for (File file : files) {
            if (shouldExcludeFile(baseFolderPath, file)) {
                continue;
            }

            try {
                addFileInformation(file, jsonsPart);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new IOException(e);
            }
        }

        String message = jsonsPart.toString();
        communicator.sendBroadcast(Constants.MSG_FOUND + message);
    }

    private boolean shouldExcludeFile(Path baseFolderPath, File file) {
        String path = baseFolderPath.relativize(file.toPath()).toString();
        for (int i = 0; i < excludedFoldersList.size(); i++) {
            String excluded = excludedFoldersList.getElementAt(i);
            if (path.startsWith(excluded + "/")) {
                return true;
            }
        }
        return false;
    }

    private void addFileInformation(File file, StringBuilder jsons)
            throws IOException, NoSuchAlgorithmException {
        List<String> fileInfo = FolderOperation.getFileInformation(file);

        if (fileInfo == null) {
            return;
        }

        String jsonData = gson.toJson(fileInfo);

        jsons.append(jsonData).append('|');
    }

    private void sleepWithRunningCheck(int totalMillis) throws InterruptedException {
        int sleepInterval = Constants.RETRY_SLEEP_MS;
        int elapsed = 0;

        while (communicator.isRunning() && elapsed < totalMillis) {
            Thread.sleep(sleepInterval);
            elapsed += sleepInterval;
        }
    }

    private void tcpReceiver() {
        try {
            tcpReceiverSocket = new ServerSocket(Constants.TCP_PORT);

            while (communicator.isRunning()) {
                Socket clientSocket = tcpReceiverSocket.accept();
                threadPool.execute(() -> {
                    try {
                        processIncomingData(clientSocket);
                    } catch (IOException e) {
                        if (!e.getMessage().equals("Socket closed")) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            releaseLocks();
            if (!e.getMessage().equals("Socket closed")) {
                e.printStackTrace();
            }
        }
    }

    private void processIncomingData(Socket clientSocket) throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            inputStream = clientSocket.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[downloader.getDynamicBuffer().getBufferSize()];
            int bytesRead;
            int totalBytes = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }

            if (totalBytes == 0) {
                System.err.println("Warning: Received 0 bytes from " + clientSocket.getRemoteSocketAddress());
                return;
            }

            byte[] data = byteArrayOutputStream.toByteArray();

            if (data.length != totalBytes) {
                System.err.println("ERROR: Data size mismatch! Expected: " + totalBytes + ", Got: " + data.length);
                return;
            }

            int id = extractIdFromData(data);
            int spaceIndex = findSpaceIndex(data);

            if (spaceIndex == 0 || spaceIndex >= data.length - 1) {
                System.err.println("ERROR: Invalid space index: " + spaceIndex + " for ID: " + id);
                return;
            }

            byte[] chunkData = Arrays.copyOfRange(data, spaceIndex + 1, data.length);

            downloadLock.lock();
            try {
                if (downloadIDControlList.get(id) != null) {
                    System.out.println("Duplicate chunk detected for ID: " + id + ", ignoring");
                    return;
                }
                downloadIDControlList.set(id, chunkData);
            } finally {
                downloadLock.unlock();
            }
        } finally {
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            clientSocket.close();
        }
    }

    private int extractIdFromData(byte[] data) {
        int index = 0;
        int id = 0;
        while (data[index] != ' ') {
            id = id * 10 + data[index++] - '0';
        }
        return id;
    }

    private int findSpaceIndex(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ' ') {
                return i;
            }
        }
        return 0;
    }

    private void udpReceiver() {
        try {
            udpReceiverSocket = new DatagramSocket(Constants.UDP_PORT);

            while (communicator.isRunning()) {
                updateBufferSize();
                udpReceiverSocket.receive(packet);
                processUdpMessage();
            }
        } catch (IOException e) {
            releaseLocks();
            if (!e.getMessage().equals("Socket closed")) {
                e.printStackTrace();
            }
        }
    }

    private void processUdpMessage() throws IOException {
        String address = packet.getAddress().toString().substring(1);
        String receivedMessage = new String(packet.getData(), 0, packet.getLength());

        if (p2p.isMessageOwner(address)) {
            return;
        }

        if (receivedMessage.startsWith(Constants.MSG_FOUND)) {
            handleFoundMessage(address, receivedMessage.substring(Constants.MSG_FOUND.length()));
        } else if (receivedMessage.startsWith(Constants.MSG_CONTROL)) {
            handleControlMessage(receivedMessage.substring(Constants.MSG_CONTROL.length()));
        } else if (receivedMessage.startsWith(Constants.MSG_CONFIRM)) {
            handleConfirmMessage(receivedMessage.substring(Constants.MSG_CONFIRM.length()));
        } else if (receivedMessage.startsWith(Constants.MSG_REQUEST)) {
            handleRequestMessage(receivedMessage.substring(Constants.MSG_REQUEST.length()));
        }
    }

    private void handleFoundMessage(String address, String message) {
        p2p.addElementToFoundList(address, message);
    }

    private void handleControlMessage(String message) throws IOException {
        String sharedFolder = p2p.getSharedFolder();
        if (sharedFolder.equals("")) {
            return;
        }

        MessageParser.ControlMessage controlMsg = new MessageParser.ControlMessage(message);

        shareFolderLock.lock();
        try {
            File folder = new File(sharedFolder);
            Path baseFolderPath = folder.toPath();
            List<File> files = FolderOperation.getAllFiles(folder);

            for (File file : files) {
                if (shouldExcludeFile(baseFolderPath, file)) {
                    continue;
                }

                if (processControlForFile(file, controlMsg)) {
                    return;
                }
            }
        } finally {
            shareFolderLock.unlock();
        }
    }

    private boolean processControlForFile(File file,
            MessageParser.ControlMessage controlMsg) {
        try {
            List<String> fileInfo = FolderOperation.getFileInformation(
                    file);

            if (fileInfo != null && fileInfo.get(2).equals(controlMsg.getHash())) {
                uploadLock.lock();
                uploadIDControlList.add(-1L);
                uploadLock.unlock();

                InetAddress addr = packet.getAddress();
                int threadNum = uploadThreadNum++;

                threadPool.execute(() -> uploader.upload(addr,
                        String.valueOf(controlMsg.getDownloadedBytes()),
                        String.valueOf(controlMsg.getId()), threadNum, file));
                return true;
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleConfirmMessage(String message) {
        MessageParser.ConfirmMessage confirmMsg = new MessageParser.ConfirmMessage(message);

        confirmLock.lock();
        if (confirmIDControlList.get(confirmMsg.getId()) == null) {
            confirmIDControlList.set(confirmMsg.getId(),
                    new SmallInformation(packet.getAddress(), confirmMsg.getNum(),
                            confirmMsg.getDownloadedBytes()));
        }
        confirmLock.unlock();
    }

    private void handleRequestMessage(String message) {
        MessageParser.RequestMessage requestMsg = new MessageParser.RequestMessage(message);

        uploadLock.lock();
        uploadIDControlList.set(requestMsg.getId(), requestMsg.getDownloadedBytes());
        uploadLock.unlock();
    }

    private void releaseLocks() {
        if (runningLock.isHeldByCurrentThread()) {
            runningLock.unlock();
        }
        if (shareFolderLock.isHeldByCurrentThread()) {
            shareFolderLock.unlock();
        }
    }

    private void updateBufferSize() {
        int newBufferSize = downloader.getDynamicBuffer().getBufferSize();
        if (buffer == null || buffer.length != newBufferSize) {
            buffer = new byte[newBufferSize];
            packet = new DatagramPacket(buffer, buffer.length);
        }
    }
}
