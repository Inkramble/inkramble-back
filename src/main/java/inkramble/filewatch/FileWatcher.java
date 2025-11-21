package inkramble.filewatch;

import inkramble.filesystem.FileSystemService;

import java.io.IOException;
import java.nio.file.*;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {

    private final FileSystemService fileSystemService;

    private final Consumer<FileChangeEvent> fileChangeEventConsumer;
    private final Thread thread;
    private final WatchService watchService;
    private final Path path;
    private final UUID id;
    private final Clock clock;
    private boolean isRunning = true;

    public static class FileWatcherException extends RuntimeException{
        public FileWatcherException(String message, Throwable cause) {super(message, cause);}
    }


    public FileWatcher(FileSystemService fileSystemService, String path, Consumer<FileChangeEvent> fileChangeEventConsumer, UUID id, Clock clock) {
        this.fileSystemService = fileSystemService;
        this.fileChangeEventConsumer = fileChangeEventConsumer;

        this.path = Paths.get(path);
        this.id = id;
        this.clock = clock;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            this.path.register(
                    watchService,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY
            );
        } catch (IOException e) {
            //watchService 등록 실패
            throw new FileWatcherException("Failed to register watch service for path: " + path,e);
        }

        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
        try {
            FileChangeEvent evt = new FileChangeEvent(
                    id,
                    fileSystemService.readDirectory(path),
                    clock.instant()
            );
            fileChangeEventConsumer.accept(evt);
        } catch (IOException e) {
            throw new FileWatcherException("File Service Error",e);
        }
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                WatchKey key = watchService.take(); // 이벤트까지 대기 (blocking)

                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();
                    FileChangeEvent evt = new FileChangeEvent(
                            id,
                            fileSystemService.readDirectory(path),
                            clock.instant()
                    );

                    if (kind == ENTRY_CREATE|| kind == ENTRY_DELETE) { //|| kind == ENTRY_MODIFY
                        fileChangeEventConsumer.accept(evt);
                    }

                    else if (kind == OVERFLOW) {
                        System.out.println("OVERFLOW: 일부 이벤트 손실 발생");
                    }
                }

                boolean valid = key.reset(); // 다음 이벤트 수신 준비
                if (!valid) {
                    System.out.println("디렉토리 감시가 더 이상 유효하지 않음");
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new FileWatcherException("File Service Error",e);
        } finally {
            try { watchService.close(); } catch (IOException ignored) {}
        }

    }

    void stop() {
        isRunning = false;
        thread.interrupt();
    }
}
