package net.chilicat.testenv.model;

import java.text.DateFormat;
import java.util.Date;

/**
 */
public class LogEntry extends Element {

    public LogEntry(String log) {
        addAttribute(Long.toString(System.currentTimeMillis()));
        addAttribute(log);
    }

    public LogEntry() {

    }

    @Override
    public String toString() {
        if (attributeCount() == 0) {
            return "LogEntry";
        }

        String date;
        try {
            Long l = Long.valueOf(attributeAt(0));
            date = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT, DateFormat.SHORT).format(new Date(l));
        } catch (NumberFormatException e) {
            // ignore
            date = "";
        }

        return date + " - " + attributeAt(1);
    }

    public long getTime() {
        return Long.valueOf(attributeAt(0));
    }
}
