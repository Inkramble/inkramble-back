package inkramble.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientManager clientManager;

    public ClientController(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
    @PostMapping("/subscribe")
    public ClientSession subscribe(@RequestBody ClientSubscribeRequest request) {
        return clientManager.subscribe(request.id(), request.rootPath());
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody ClientSubscribeRequest request) {
        clientManager.unsubscribe(request.id());
        return ResponseEntity.ok().build();
    }



}
