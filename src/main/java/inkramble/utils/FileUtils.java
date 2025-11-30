package inkramble.utils;

import inkramble.filesystem.file.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("File Name Error : " + fileName);

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "FILE";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    public static String getExtension(Path path) {
        if (path == null) throw new IllegalArgumentException("Path is null");
        if (Files.isDirectory(path)) throw new IllegalStateException("Path is a directory: " + path);

        Path fileName = path.getFileName();

        if (fileName == null) throw new IllegalStateException("File does not exist : " + path);

        return getExtension(fileName.toString());
    }

    public static boolean isInkFile(String fileName) {
        return getExtension(fileName).equals("ink");
    }

    public static boolean isInkFile(Path path){
        return getExtension(path).equals("ink");
    }

    public static FileInfo getFileInfo(String path) throws IOException {
        return getFileInfo(path);
    }

    public static FileInfo getFileInfo(Path path) throws IOException {

        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

        return new FileInfo(
                path.getFileName().toString(),
                attrs.creationTime().toInstant(),
                attrs.lastModifiedTime().toInstant()
        );
    }


    public static String makeNewFile(String pathString) throws IOException {
        //pathString 하위에 새로운 파일 만들기
        String fileName = "File ";
        int index = 1;
        while(true){
            String tempFileName = pathString + "/" + fileName + index + ".ink";
            File file = new File(tempFileName);
            Path path = Paths.get(tempFileName);
            if (!file.exists()){
                Files.writeString(path,"", StandardOpenOption.CREATE_NEW);
                return tempFileName;
            }
            index++;
        }
    }

    public static String makeNewDirectory(String pathString) throws IOException {
        String dirName = "Directory ";
        int index = 1;

        while (true) {
            Path dirPath = Paths.get(pathString, dirName + index);

            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
                return dirPath.toString();
            }
            index++;
        }
    }
}
