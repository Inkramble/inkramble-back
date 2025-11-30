package inkramble.filesystem.file;

import java.util.List;
import java.util.Map;

public record InkFileData(
        InkMetadata metadata,
        List<Map<String, Object>> contents,
        String textContents
) {
}
