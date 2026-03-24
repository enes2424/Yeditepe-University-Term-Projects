import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FileUploader {
    private final ReentrantLock uploadLock;
    private final ArrayList<Long> uploadIDControlList;
    private final NetworkCommunicator communicator;

    public FileUploader(ReentrantLock uploadLock, ArrayList<Long> uploadIDControlList,
            NetworkCommunicator communicator) {
        this.uploadLock = uploadLock;
        this.uploadIDControlList = uploadIDControlList;
        this.communicator = communicator;
    }

    public void upload(InetAddress address,
            String downloadedBytes,
            String targetID, int threadNum, java.io.File file) {
        try {
            sendConfirmation(address, downloadedBytes, targetID, threadNum);

            long requestFirstBytesIndex = waitForUploadRequest(threadNum);
            if (requestFirstBytesIndex == -1) {
                return;
            }

            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            byte[] dataToSend = prepareUploadData(fileBytes, requestFirstBytesIndex, targetID);
            sendData(address, dataToSend);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendConfirmation(InetAddress address, String downloadedBytes,
            String targetID, int threadNum) throws IOException {
        String confirmMsg = MessageParser.buildConfirmMessage(
                Long.parseLong(downloadedBytes), Integer.parseInt(targetID), threadNum);
        communicator.sendPrivate(Constants.MSG_CONFIRM + confirmMsg, address);
    }

    private long waitForUploadRequest(int threadNum) throws InterruptedException {
        for (int attempt = 0; attempt < Constants.UPLOAD_RETRY_COUNT; attempt++) {
            uploadLock.lock();
            Long requestIndex = uploadIDControlList.get(threadNum);
            uploadLock.unlock();

            if (requestIndex != null && requestIndex != -1L) {
                return requestIndex;
            }

            Thread.sleep(Constants.RETRY_SLEEP_MS);
        }
        return -1;
    }

    private byte[] prepareUploadData(byte[] bytes, long startIndex, String targetID) {
        String messagePrefix = targetID + " ";
        byte[] prefixBytes = messagePrefix.getBytes();

        if (startIndex < 0 || startIndex >= bytes.length) {
            System.err.println("Invalid startIndex: " + startIndex + ", file size: " + bytes.length);
            return new byte[prefixBytes.length];
        }

        int dataSize = (int) Math.min(bytes.length - startIndex,
                Constants.CHUNK_SIZE);

        if (dataSize < 0) {
            System.err.println("Negative dataSize: " + dataSize);
            return new byte[prefixBytes.length];
        }

        byte[] buffer = new byte[prefixBytes.length + dataSize];
        System.arraycopy(prefixBytes, 0, buffer, 0, prefixBytes.length);

        int bufferOffset = prefixBytes.length;

        System.arraycopy(bytes, (int) startIndex, buffer, bufferOffset, dataSize);

        return buffer;
    }

    private void sendData(InetAddress address, byte[] data) throws IOException {
        try (Socket socket = new Socket(address, Constants.TCP_PORT);
                OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write(data);
            outputStream.flush();
            socket.shutdownOutput();
        }
    }
}
