package inkramble.filesystem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemService {
    public FileNode readDirectory(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다: " + dirPath);
        }
        return buildFileNode(path, path);
    }

    private FileNode buildFileNode(Path path, Path basePath) throws IOException {
        boolean isDirectory = Files.isDirectory(path);
        String contentType = isDirectory ? "directory" : Files.probeContentType(path);
        String relativePath = basePath.relativize(path).toString();
        List<FileNode> children = List.of();

        if (isDirectory) {
            try (var stream = Files.list(path)) {
                children = stream
                        .map(p -> {
                            try {
                                return buildFileNode(p,basePath);
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
                contentType != null ? contentType : "unknown",
                children
        );
    }
}
