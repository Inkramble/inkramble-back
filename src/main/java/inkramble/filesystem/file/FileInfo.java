package inkramble.filesystem.file;

import java.time.Instant;

public record FileInfo(
        String fileName,
        Instant createdAt,
        Instant updateAt

        ) {
}
