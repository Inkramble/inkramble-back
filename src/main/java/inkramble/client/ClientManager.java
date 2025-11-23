package inkramble.client;

import inkramble.filewatch.FileWatchService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientManager {
    private final Map<UUID, ClientSession> sessions;
    private final FileWatchService fileWatchService;

    public ClientManager(FileWatchService fileWatchService) {
        this.fileWatchService = fileWatchService;
        sessions = new ConcurrentHashMap<>();
    }

    public ClientSession getOrCreate(UUID id) {
        return sessions.computeIfAbsent(id, k -> new ClientSession());

    }



    public void updateRootPath(UUID id, String rootPath) {
        ClientSession s = getOrCreate(id);
        s.setRootPath(rootPath);
        // 변경 알림
        sendEvent(id, "rootPathChanged", rootPath);
        try {
            fileWatchService.update(id, rootPath, e -> sendEvent(id, "fs.fileChanged", e));
        } catch (Exception ignored) {
        }
    }

    public SseEmitter stream(UUID id) {
        return sessions.get(id).getSubscribers().getFirst();
    }

    public ClientSession subscribe(UUID id, String rootPath) {
        ClientSession s = getOrCreate(id);
        s.setRootPath(rootPath);

        SseEmitter emitter = new SseEmitter(0L);
        s.clearSubscribers();
        s.addSubscriber(emitter);

        //브라우저 탭 닫힘
        //네트워크 단절
        //서버 타임아웃
        emitter.onTimeout(() -> s.removeSubscriber(emitter));
        emitter.onCompletion(() -> s.removeSubscriber(emitter));
        emitter.onError((e) -> s.removeSubscriber(emitter));

        SseEmitter.SseEventBuilder evt = SseEmitter.event()
                .name("connected")
                .id(id.toString())
                .data("ok@" + Instant.now());

        //event: connected

        //id: <UUID>
        //data: ok@2025-11-07T18:00:00Z

        try {
            fileWatchService.watch(id, rootPath, e -> sendEvent(id, "fileChanged", e));
        } catch (Exception ignored){

        }

        return s;

    }

    public void sendEvent(UUID id, String eventName, Object data) {
        ClientSession s = sessions.get(id);
        if (s == null) return;

        for (SseEmitter emitter : s.getSubscribers()) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                s.getSubscribers().remove(emitter);
            }
        }
    }

    public void unsubscribe(UUID id) {
        ClientSession s = sessions.get(id);
        if (s == null) return;

        // 모든 emitter 정리
        for (SseEmitter emitter : s.getSubscribers()) {
            try {
                emitter.complete();   // SSE 스트림 종료
            } catch (Throwable ignored) {}
        }

        s.clearSubscribers();
    }
}
