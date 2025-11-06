package inkramble.dev;

import inkramble.config.DevProperties;
import inkramble.filesystem.FileNode;
import inkramble.filesystem.FileSystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {
    private final DevProperties devProperties;

    public TestController(DevProperties devProperties) {
        this.devProperties = devProperties;
    }

    @GetMapping("/test")
    public FileNode getDirectory() throws IOException {
        FileSystemService fileSystemService = new FileSystemService();
        return fileSystemService.readDirectory(devProperties.getStorage().getRootPath());
    }
}
