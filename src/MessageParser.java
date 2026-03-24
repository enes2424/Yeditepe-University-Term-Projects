public class MessageParser {
    public static class FoundMessage {
        private final String[] allFileInfo;

        public FoundMessage(String message) {
            if (message.isEmpty()) {
                this.allFileInfo = new String[0];
                return;
            }

            this.allFileInfo = message.split("\\|");
        }

        public String[] getAllFileInfo() {
            return allFileInfo;
        }
    }

    public static class ControlMessage {
        private final long downloadedBytes;
        private final int id;
        private final String hash;

        public ControlMessage(String message) {
            String[] parts = message.split(";");
            this.downloadedBytes = Long.parseLong(parts[0]);
            this.id = Integer.parseInt(parts[1]);
            this.hash = parts[2];
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public int getId() {
            return id;
        }

        public String getHash() {
            return hash;
        }
    }

    public static class ConfirmMessage {
        private final long downloadedBytes;
        private final int id;
        private final int num;

        public ConfirmMessage(String message) {
            String[] parts = message.split(";");
            this.downloadedBytes = Long.parseLong(parts[0]);
            this.id = Integer.parseInt(parts[1]);
            this.num = Integer.parseInt(parts[2]);
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public int getId() {
            return id;
        }

        public int getNum() {
            return num;
        }
    }

    public static class RequestMessage {
        private final long downloadedBytes;
        private final int id;

        public RequestMessage(String message) {
            String[] parts = message.split(";");
            this.downloadedBytes = Long.parseLong(parts[0]);
            this.id = Integer.parseInt(parts[1]);
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public int getId() {
            return id;
        }
    }

    public static String buildControlMessage(long downloadedBytes, int id, String hash) {
        return downloadedBytes + ";" + id + ";" + hash;
    }

    public static String buildConfirmMessage(long downloadedBytes, int targetId, int threadNum) {
        return downloadedBytes + ";" + targetId + ";" + threadNum;
    }

    public static String buildRequestMessage(long downloadedBytes, int id) {
        return downloadedBytes + ";" + id;
    }
}
