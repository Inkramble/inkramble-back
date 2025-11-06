package inkramble.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("File Name Error : " + fileName);

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }

        throw new IllegalStateException("No extension found: " + fileName);
    }

    public static String getExtension(Path path) {
        if (path == null) throw new IllegalArgumentException("Path is null");
        if (Files.isDirectory(path)) throw new IllegalStateException("Path is a directory: " + path);

        Path fileName = path.getFileName();

        if (fileName == null) throw new IllegalStateException("File does not exist : " + path);

        return getExtension(fileName.toString());
    }
}
