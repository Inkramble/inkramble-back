package inkramble;

import inkramble.filesystem.FileSystemService;
import inkramble.filewatch.FileChangeEvent;
import inkramble.utils.FileUtils;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class Test {
    public static void main(String[] args) throws IOException {
//        FileSystemService fileSystemService = new FileSystemService();
//
//        FileChangeEvent e = new FileChangeEvent(
//                UUID.randomUUID(),
//                fileSystemService.readDirectory("C:\\Users\\USER\\Documents\\inkramble"),
//                Instant.now()
//        );
//        System.out.println(e);
        String extension = FileUtils.getExtension(".txt");
        System.out.println(extension);

    }
}
