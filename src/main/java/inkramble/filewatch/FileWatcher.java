package inkramble.filewatch;

import inkramble.filesystem.FileSystemService;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {

    private final FileSystemService fileSystemService;

    private final Consumer<FileChangeEvent> fileChangeEventConsumer;
    private final Thread thread;
    private final WatchService watchService;
    private final String rootPathString;
    private final UUID id;
    private final Clock clock;
    private volatile boolean isRunning = true;


    private final Map<WatchKey, Path> keyDirectoryMap = new ConcurrentHashMap<>();

    public static class FileWatcherException extends RuntimeException {
        public FileWatcherException(String message, Throwable cause) { super(message, cause); }
    }

    public FileWatcher(FileSystemService fileSystemService,
                       String path,
                       Consumer<FileChangeEvent> fileChangeEventConsumer,
                       UUID id,
                       Clock clock) {
        this.fileSystemService = fileSystemService;
        this.fileChangeEventConsumer = fileChangeEventConsumer;

        this.rootPathString = path;
        Path rootPath = Paths.get(path);
        this.id = id;
        this.clock = clock;

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            registerAll(rootPath);
        } catch (IOException e) {
            throw new FileWatcherException("File Service Error : " + path, e);
        }

        this.thread = new Thread(this, "FileWatcher " + id);
        this.thread.setDaemon(true);
        this.thread.start();

        emitChangeEvent();
    }


    private void emitChangeEvent() {
        try {
            FileChangeEvent evt = new FileChangeEvent(
                    id,
                    fileSystemService.readDirectory(rootPathString),
                    clock.instant()
            );
            fileChangeEventConsumer.accept(evt);
        } catch (IOException e) {
            throw new FileWatcherException("File Service Error", e);
        }
    }


    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(
                watchService,
                ENTRY_CREATE,
                ENTRY_DELETE,
                ENTRY_MODIFY
        );
        keyDirectoryMap.put(key, dir);
    }

    private void registerAll(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    if (!isRunning) break;
                    Thread.currentThread().interrupt();
                    break;
                }

                Path dir = keyDirectoryMap.get(key);
                if (dir == null) {
                    key.reset();
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) {
                        System.out.println("OVERFLOW: 일부 이벤트 손실 발생");
                        continue;
                    }


                    Path name = (Path) event.context();
                    Path child = dir.resolve(name);


                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child)) {
                                registerAll(child);
                            }
                        } catch (IOException e) {
                            throw new FileWatcherException("File Service Error : "+child, e);
                        }
                    }


                    if (kind == ENTRY_CREATE || kind == ENTRY_DELETE) {
                        emitChangeEvent();
                    }
                }

                boolean valid = key.reset();

                if (!valid) {
                    keyDirectoryMap.remove(key);
                    if (keyDirectoryMap.isEmpty()) {//FileWatcher 종료
                        break;
                    }
                }
            }
        } finally {
            try {
                watchService.close();
            } catch (IOException ignored) {}
        }
    }

    void stop() {
        isRunning = false;
        thread.interrupt();
    }
}
