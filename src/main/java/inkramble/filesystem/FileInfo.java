package inkramble.filesystem;

import java.time.Instant;

public record FileInfo(
        String fileName,
        Instant createdAt,
        Instant updateAt

        ) {
}
