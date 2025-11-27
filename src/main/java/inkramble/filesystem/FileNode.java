package inkramble.filesystem;

import java.util.List;

public record FileNode(
    String name,
    String path,
    boolean isDirectory,
    String contentType,
    List<FileNode> children
){}
