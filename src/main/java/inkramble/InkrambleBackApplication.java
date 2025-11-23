package inkramble;

import inkramble.filesystem.FileSystemService;
import inkramble.filewatch.FileChangeEvent;
import inkramble.filewatch.FileWatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Clock;
import java.util.UUID;

@SpringBootApplication
public class InkrambleBackApplication {

    public static void main(String[] args) throws IOException {

        SpringApplication.run(InkrambleBackApplication.class, args);

    }

}
