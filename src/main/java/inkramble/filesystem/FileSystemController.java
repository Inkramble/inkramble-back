package inkramble.filesystem;

import inkramble.client.ClientManager;
import inkramble.client.ClientSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/filesystem")
public class FileSystemController {
    private final FileSystemService fileSystemService;
    private final ClientManager clientManager;

    public FileSystemController(FileSystemService fileSystemService, ClientManager clientManager) {
        this.fileSystemService = fileSystemService;
        this.clientManager = clientManager;
    }

    @GetMapping("/read")
    public ResponseEntity<?> readFile(  @RequestParam UUID id,
                           @RequestParam String path){

        Optional<ClientSession> session = clientManager.getSession(id);
        if(session.isEmpty()){
            return ResponseEntity
                    .status(404)
                    .body("No session found for id: " + id);
        }
        Path dirPath = Paths.get(session.get().getRootPath(), path);

        String data;
        try {
            data = fileSystemService.readFile(dirPath);
        } catch (IOException e) {
            return ResponseEntity
                    .status(400)
                    .body("cannot read file: " + dirPath.toString());
        }
        return ResponseEntity.ok(data);

    }

    @PostMapping("/save")
    public void saveFile(@RequestParam FileSystemRequest request){

    }
}
