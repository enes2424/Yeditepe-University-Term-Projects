public class Constants {
    public static final int UDP_PORT = 9090;
    public static final int TCP_PORT = 8080;
    public static final String BROADCAST_ADDRESS = "255.255.255.255";

    public static final int CHUNK_SIZE = 256 * 1024;
    public static final int BUFFER_SIZE = 65507;

    public static final int CONFIRM_RETRY_COUNT = 20;
    public static final int REQUEST_RETRY_COUNT = 30;
    public static final int UPLOAD_RETRY_COUNT = 40;
    public static final int RETRY_SLEEP_MS = 300;
    public static final int SHARE_FOLDER_INTERVAL_MS = 3000;

    public static final String MSG_FOUND = "FOUND ";
    public static final String MSG_CONTROL = "CONTROL ";
    public static final String MSG_CONFIRM = "CONFIRM ";
    public static final String MSG_REQUEST = "REQUEST ";

    public static final String APP_TITLE_PREFIX = "P2P";
    public static final int WINDOW_WIDTH = 575;
    public static final int WINDOW_HEIGHT = 800;

    public static final long MIN_BYTES_TO_START = 2 * 1024 * 1024;

    public static final int MAX_TTL = 16;

    private Constants() {
    }
}
