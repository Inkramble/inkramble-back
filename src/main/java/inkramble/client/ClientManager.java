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


    // TODO : 나중에 FileSystem관련 기능으로 옮길 것
    public void updateRootPath(UUID id, String rootPath) {
        ClientSession s = getOrCreate(id);
        s.setRootPath(rootPath);
        // 변경 알림
        sendEvent(id, "rootPathChanged", rootPath);
    }

    public ClientSession subscribe(UUID id, String rootPath) {
        ClientSession s = getOrCreate(id);
        s.setRootPath(rootPath);

        SseEmitter emitter = new SseEmitter();
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
            emitter.send(evt);

            //TODO : SSE emitter 초기 data send // 또는 처음에는 API로 처리하기
        } catch (IOException ignored) {
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
