package inkramble.filesystem.file;

import java.util.List;

public record InkMetadata(
        String category,
        List<String> tags
) {
}
