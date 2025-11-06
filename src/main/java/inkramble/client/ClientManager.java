package inkramble.client;

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

    public ClientManager() {
        sessions = new ConcurrentHashMap<>();
    }

    public ClientSession getOrCreate(UUID id) {
        return sessions.computeIfAbsent(id, k -> new ClientSession());
    }

    // TODO : 나중에 FileSYstem관련 기능으로 옮길 것
    public void updateRootPath(UUID id, String rootPath) {
        ClientSession s = getOrCreate(id);
        s.setRootPath(rootPath);
        // 변경 알림
        sendEvent(id, "rootPathChanged", rootPath);
    }

    public SseEmitter subscribe(UUID id, long timeoutMillis) {
        ClientSession s = getOrCreate(id);
        SseEmitter emitter = new SseEmitter();
        s.addSubscriber(emitter);

        //브라우저 탭 닫힘
        //네트워크 단절
        //서버 타임아웃
        emitter.onTimeout(() -> s.removeSubscriber(emitter));
        emitter.onCompletion(() -> s.removeSubscriber(emitter));
        emitter.onError((e) -> s.removeSubscriber(emitter));

        try {
            SseEmitter.SseEventBuilder evt = SseEmitter.event()
                    .name("connected")
                    .id(id.toString())
                    .data("ok@" + Instant.now());

            //event: connected
            //id: <UUID>
            //data: ok@2025-11-07T18:00:00Z
            emitter.send(evt);
            String rootPath = s.getRootPath();
            if (rootPath != null) {
                //TODO : SSE emitter 초기 data send // 또는 처음에는 API로 처리하기
            }
        } catch (IOException ignored) {}

        return emitter;
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

}
