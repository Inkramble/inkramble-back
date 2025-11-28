package inkramble.filesystem;

import inkramble.client.ClientManager;
import inkramble.client.ClientSession;
import inkramble.filesystem.file.FileInfo;
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
    private record FileReadResponse(
            FileInfo info,
            String data
    ) {}

    public FileSystemController(FileSystemService fileSystemService, ClientManager clientManager) {
        this.fileSystemService = fileSystemService;
        this.clientManager = clientManager;
    }

    @GetMapping("/read")
    public ResponseEntity<?> readFile(@RequestParam UUID id,
                                      @RequestParam String path) {

        Optional<ClientSession> session = clientManager.getSession(id);

        if (session.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("No session found for id: " + id);
        }

        Path dirPath = Paths.get(session.get().getRootPath(), path);

        FileResponse response;

        try {
            response = fileSystemService.readFile(dirPath);
        } catch (IOException e) {
            return ResponseEntity
                    .status(400)
                    .body("cannot read file: " + dirPath.toString());
        }

        return ResponseEntity.ok(response);

    }

    @PostMapping("/save")
    public ResponseEntity<String> saveFile(@RequestBody FileSystemRequest request) {
        Optional<ClientSession> session = clientManager.getSession(request.id());

        if (session.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("No session found for id: " + request.id());
        }

        Path dirPath = Paths.get(session.get().getRootPath(), request.path());

        try {
            fileSystemService.saveFile(dirPath, request.data());
        } catch (IOException e) {
            System.out.println(dirPath.toString());
            System.out.println(e.getMessage());
            return ResponseEntity
                    .status(400)
                    .body("No session found for id: " + request.id());
        }

        return ResponseEntity
                .status(200)
                .body("Saved file: " + dirPath.toString());
    }
}
