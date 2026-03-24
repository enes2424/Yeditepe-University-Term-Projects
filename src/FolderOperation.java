import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.DefaultListModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;

public class FolderOperation {
    public static long numOfBytes;
    public static byte[] bytes;
    public static DefaultListModel<String> excludedFoldersList;

    public static List<String> getFileInformation(File file)
            throws IOException, NoSuchAlgorithmException {
        Path filePath = file.toPath();
        bytes = Files.readAllBytes(filePath);
        String hash = getFileHash(bytes);

        return List.of(file.getName(), String.valueOf(bytes.length), hash);
    }

    public static List<File> getAllFiles(File folder) {
        List<File> fileList = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory())
                    fileList.addAll(getAllFiles(file));
                else if (file.isFile()) {
                    fileList.add(file);
                }
            }
        return fileList;
    }

    public static List<String> getAllFolders(DefaultListModel<String> listModel, Path baseFolderPath, File folder) {
        List<String> folderList = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null)
            for (File file : files)
                if (file.isDirectory() && !listModel.contains(baseFolderPath.relativize(file.toPath()).toString())) {
                    folderList.add(baseFolderPath.relativize(file.toPath()).toString());
                    folderList.addAll(getAllFolders(listModel, baseFolderPath, file));
                }
        return folderList;
    }

    private static String getFileHash(byte[] fileBytes) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static boolean isMatchedFileAndMasks(String fileName, DefaultListModel<String> excludedMasksList) {
        for (int i = 0; i < excludedMasksList.size(); i++) {
            String mask = excludedMasksList.getElementAt(i);
            if (isMatchedFileAndMask(fileName, mask)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatchedFileAndMask(String fileName, String mask) {
        int i1 = 0, i2 = 0, len1 = fileName.length(), len2 = mask.length();
        while (i1 < len1 && i2 < len2) {
            if (mask.charAt(i2) == '*')
                break;
            if (mask.charAt(i2) != '?' && fileName.charAt(i1) != mask.charAt(i2)) {
                return false;
            }
            i1++;
            i2++;
        }
        while (i2 < len2 && mask.charAt(i2) == '*')
            i2++;
        if (i1 == len1)
            return i2 == len2;
        if (i2 == len2)
            return mask.charAt(i2 - 1) == '*';
        while (true) {
            if (fileName.charAt(i1) == mask.charAt(i2) &&
                    isMatchedFileAndMask(fileName.substring(i1), mask.substring(i2)))
                return true;
            if (++i1 == len1)
                return false;
        }
    }
}
