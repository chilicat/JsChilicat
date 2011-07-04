package net.chilicat.testenv.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

/**
 */
final class VerboseLoggerMessageBus extends AbstractMessageBus {

    private final Logger log = Logger.getLogger("Verbose");

    VerboseLoggerMessageBus() {
        super(false);
        configureLogger(log);
    }

    @Override
    public void print(String message) {
        log.info(message);
    }

    @Override
    public void println(String message) {
        log.info(message);
    }

    @Override
    public void log(String msg) {
        log.info(msg);
    }

    private static void configureLogger(Logger logger) {
        logger.setUseParentHandlers(false);
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            final Map<Level, String> map = createLevelMap();

            public String format(LogRecord record) {
                //return map.get(record.getLevel()) + record.getMessage() + "\n";
                return record.getMessage() + "\n";
            }
        });
        logger.addHandler(handler);
    }

    private static Map<Level, String> createLevelMap() {
        int length = 0;
        final List<Level> levelList = Arrays.asList(Level.INFO, Level.SEVERE, Level.WARNING);
        for (Level level : levelList) {
            length = Math.max(level.toString().length(), length);
        }
        final Map<Level, String> map = new HashMap<Level, String>();
        for (Level level : levelList) {
            String str = level.toString() + ":";
            int diff = length - str.length();
            if (diff != 0) {
                for (int i = 0; i < diff; i++) {
                    str += " ";
                }
            }
            map.put(level, str);
        }
        return map;
    }
}
