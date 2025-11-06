package inkramble;

import inkramble.filesystem.FileNode;
import inkramble.filesystem.FileSystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {
    @GetMapping("/test")
    public FileNode getDirectory() throws IOException {
        FileSystemService fileSystemService = new FileSystemService();
        return fileSystemService.readDirectory(TestConstants.getRootPath());
    }
}
