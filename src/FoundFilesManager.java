import com.google.gson.Gson;

import java.util.List;

public class FoundFilesManager {
    private final Gson gson = new Gson();
    private final List<FileInfo> allListFileInfo;
    private final List<String> allFoundFiles;

    public FoundFilesManager(
            List<FileInfo> allListFileInfo,
            List<String> allFoundFiles) {
        this.allListFileInfo = allListFileInfo;
        this.allFoundFiles = allFoundFiles;
    }

    public void updateFoundFiles(String address, String receivedMessage) {
        if (receivedMessage.isEmpty()) {
            removeFilesFromAddress(address);
            return;
        }

        MessageParser.FoundMessage foundMsg = new MessageParser.FoundMessage(receivedMessage);
        String[] allFileInfo = foundMsg.getAllFileInfo();

        updateExistingFiles(address, allFileInfo);
        addNewFiles(address, allFileInfo);
    }

    private void removeFilesFromAddress(String address) {
        for (int i = 0; i < allFoundFiles.size(); i++) {
            if (allFoundFiles.get(i).contains(address)) {
                allListFileInfo.remove(i);
                allFoundFiles.remove(i--);
            }
        }
    }

    private void updateExistingFiles(String address, String[] allFileInfo) {
        int fileIndex = 0;

        for (int i = 0; i < allFoundFiles.size(); i++) {
            if (!allFoundFiles.get(i).contains(address)) {
                continue;
            }

            if (fileIndex >= allFileInfo.length) {
                allListFileInfo.remove(i);
                allFoundFiles.remove(i--);
            } else {
                FileInfo result = processFile(allFileInfo[fileIndex]);

                allListFileInfo.set(i, result);
                allFoundFiles.set(i, result.filename + " " + P2P.formatBytes(result.bytes) + " from " + address);
                fileIndex++;
            }
        }
    }

    private void addNewFiles(String address, String[] allFileInfo) {
        int startIndex = findStartIndexInList(address);

        for (int i = startIndex; i < allFileInfo.length; i++) {
            FileInfo result = processFile(allFileInfo[i]);
            if (result.bytes == 0) {
                continue;
            }

            allListFileInfo.add(result);
            allFoundFiles.add(result.filename + " " + P2P.formatBytes(result.bytes) + " from " + address);
        }
    }

    private int findStartIndexInList(String address) {
        int count = 0;
        for (int i = 0; i < allFoundFiles.size(); i++) {
            if (allFoundFiles.get(i).contains(address)) {
                count++;
            }
        }
        return count;
    }

    private FileInfo processFile(String fileInfo) {
        @SuppressWarnings("unchecked")
        List<String> data = gson.fromJson(fileInfo, List.class);
        String filename = data.get(0);
        String byteStr = data.get(1);
        String hash = data.get(2);

        return new FileInfo(filename, Long.parseLong(byteStr), hash);
    }
}
