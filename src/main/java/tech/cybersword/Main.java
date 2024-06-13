package tech.cybersword;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            System.out.println("Usage: java -jar tech.cybersword.usb-*.jar <path>");
            System.exit(1);
        }

        if (logger.isInfoEnabled()) {
            logger.info("start directory watcher");
        }

        DirectoryWatcher watcher = new DirectoryWatcher();
        watcher.observe(args[0]);
    }
}