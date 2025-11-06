package inkramble.filesystem;

import java.util.List;
import java.util.UUID;

public record FileNode(
    String name,
    String path,
    boolean isDirectory,
    String contentType,
    List<FileNode> children
){}
