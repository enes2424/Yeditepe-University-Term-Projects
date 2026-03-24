import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkCommunicator {
    private DatagramSocket senderSocket;
    private final InetAddress broadcastAddress;
    private final ReentrantLock runningLock;
    private boolean running;

    public NetworkCommunicator(ReentrantLock runningLock) throws UnknownHostException {
        this.broadcastAddress = InetAddress.getByName(Constants.BROADCAST_ADDRESS);
        this.runningLock = runningLock;
        this.running = false;
    }

    public void initialize() throws IOException {
        senderSocket = new DatagramSocket();
        senderSocket.setBroadcast(true);
        try {
            senderSocket.setOption(java.net.StandardSocketOptions.IP_MULTICAST_TTL, Constants.MAX_TTL);
        } catch (SocketException e) {
            System.err.println("Warning: Could not set TTL: " + e.getMessage());
        }
    }

    public void setRunning(boolean running) {
        runningLock.lock();
        this.running = running;
        runningLock.unlock();
    }

    public boolean isRunning() {
        runningLock.lock();
        boolean status = this.running;
        runningLock.unlock();
        return status;
    }

    public void sendBroadcast(String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                broadcastAddress, Constants.UDP_PORT);
        senderSocket.send(packet);
    }

    public void sendPrivate(String message, InetAddress address) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                address, Constants.UDP_PORT);
        senderSocket.send(packet);
    }

    public void close() {
        if (senderSocket != null && !senderSocket.isClosed()) {
            senderSocket.close();
        }
    }
}
