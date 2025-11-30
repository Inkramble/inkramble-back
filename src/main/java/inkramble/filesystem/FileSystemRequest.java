package inkramble.filesystem;

import java.nio.file.Path;
import java.util.UUID;

public record FileSystemRequest(
        UUID id,
        String path,
        String data
) {
}
