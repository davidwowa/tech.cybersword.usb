package tech.cybersword;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * based on ChatGPT's code
 */
public class DirectoryWatcher {

    private static final Logger logger = LogManager.getLogger(DirectoryWatcher.class);

    private static Map<WatchKey, Path> keys = new HashMap<>();

    public void observe(String pathRoot) throws IOException, InterruptedException {
        // Pfad zum Hauptverzeichnis
        Path dir = Paths.get(pathRoot); // Passen Sie dies an Ihren Pfad an

        // WatchService initialisieren
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            registerAll(dir, watcher);

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Watch Service registered for dir: %s", dir));
            }

            // Endlos-Schleife, um auf Ereignisse zu warten
            while (true) {
                WatchKey key = watcher.take();

                Path path = keys.get(key);
                if (path == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("WatchKey not recognized!");
                    }
                    System.err.println("WatchKey not recognized!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // Context für das Verzeichnis-Event ist relativ zum Verzeichnis
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path name = ev.context();
                    Path child = path.resolve(name);

                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("%s: %s", kind.name(), child));
                    }

                    // Wenn das Verzeichnis neu erstellt wird, registriere es und seine Unterordner
                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child)) {
                                registerAll(child, watcher);
                            }
                        } catch (IOException x) {
                            // Fehlerbehandlung
                        }
                    }
                }

                // Schlüssel zurücksetzen
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // Alle Verzeichnisse sind nicht zugänglich
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    // Hilfsmethode, um alle Verzeichnisse rekursiv zu registrieren
    private static void registerAll(final Path start, final WatchService watcher) throws IOException {
        // Verwende einen Depth-First-Search (DFS) Ansatz mit Files.walkFileTree
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir, watcher);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Registriere ein einzelnes Verzeichnis
    private static void registerDirectory(Path dir, WatchService watcher) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        keys.put(key, dir);
    }
}