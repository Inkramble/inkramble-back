package inkramble.filesystem;

import inkramble.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileSystemService {
    public FileNode readDirectory(Path dirPath) throws IOException {

        if (!Files.exists(dirPath)) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다: " + dirPath);
        }
        return buildFileNode(dirPath, dirPath);
    }
    public FileNode readDirectory(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        return readDirectory(path);
    }

    private FileNode buildFileNode(Path path, Path basePath) throws IOException {
        boolean isDirectory = Files.isDirectory(path);
        String contentType = isDirectory ? "directory" : FileUtils.getExtension(path);
        String relativePath = basePath.relativize(path).toString();
        List<FileNode> children = List.of();

        if (isDirectory) {
            try (var stream = Files.list(path)) {
                children = stream
                        .map(p -> {
                            try {
                                return buildFileNode(p, basePath);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        })
                        .collect(Collectors.toList());
            }
        }

        return new FileNode(
                path.getFileName().toString(),
                relativePath,
                isDirectory,
                contentType,
                children
        );
    }

    public String readFile(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        return readFile(path);
    }

    public String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public void saveFile(String pathString, String data) throws IOException {
        Path path = Paths.get(pathString);
        Files.writeString(path, data);
    }

    public void saveFile(Path path, String data) throws IOException {
        Files.writeString(path, data);
    }

    public String makeNewFile(String pathString) throws IOException {
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

    public String makeNewDirectory(String pathString) throws IOException {
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
