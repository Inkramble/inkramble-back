package inkramble.filewatch;

import inkramble.filesystem.FileSystemService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


@Component
public class FileWatchService {
    private final FileSystemService fileSystemService;
    private final Clock clock;


    private final Map<UUID, FileWatcher> watchers;


    public FileWatchService(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
        clock = Clock.systemUTC();
        watchers = new ConcurrentHashMap<>();
    }

    // 새롭게 연결하는 경우
    public void watch(UUID clientId, String rootPath, Consumer<FileChangeEvent> fileChangeEventConsumer) {

        unwatch(clientId);
        Path base = Paths.get(rootPath);
        FileWatcher watcher;

        try {
            watcher = new FileWatcher(fileSystemService, rootPath, fileChangeEventConsumer, clientId,clock);
        } catch (FileWatcher.FileWatcherException e) {
            throw new RuntimeException(e);
        }
        watchers.put(clientId, watcher);
    }

    public void unwatch(UUID clientId) {
        FileWatcher old = watchers.remove(clientId);
        if (old != null) old.stop();
    }

    public void update(UUID clientId, String rootPath, Consumer<FileChangeEvent> fileChangeEventConsumer) {
        watch(clientId, rootPath, fileChangeEventConsumer);
    }

    @PreDestroy
    public void shutdown() {
        watchers.values().forEach(FileWatcher::stop);
        watchers.clear();
    }


}
