package inkramble.filewatch;

import inkramble.filesystem.FileNode;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public record FileChangeEvent(
        UUID id,
        FileNode fileNode,
        Instant at
) {
}
