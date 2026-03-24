import java.net.InetAddress;

public class SmallInformation {
    private final InetAddress address;
    private final Long downloadedBytes;
    private final int num;

    public SmallInformation(InetAddress address, int num, Long downloadedBytes) {
        this.num = num;
        this.address = address;
        this.downloadedBytes = downloadedBytes;
    }

    public int getNum() {
        return num;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Long getDownloadedBytes() {
        return downloadedBytes;
    }
}
