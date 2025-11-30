package inkramble.filesystem;

import inkramble.filesystem.file.FileInfo;
import inkramble.filesystem.file.InkFileData;

public record FileResponse(
        String filename,
        FileInfo info,
        InkFileData data //ink면 inkData, 아니면 contents만 담아서
) {
}


