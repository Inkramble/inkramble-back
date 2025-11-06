package inkramble.client;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ClientSession {
    // Read & Write가 atomic 연산
    private final AtomicReference<String> rootPath = new AtomicReference<>();
    private final CopyOnWriteArrayList<SseEmitter> subscribers = new CopyOnWriteArrayList<>();

    public String getRootPath() { return rootPath.get(); }
    public void setRootPath(String path) { rootPath.set(path); }

    public List<SseEmitter> getSubscribers() { return subscribers; }
    public void addSubscriber(SseEmitter subscriber) { this.subscribers.add(subscriber); }
    public void removeSubscriber(SseEmitter subscriber) { this.subscribers.remove(subscriber); }

}
