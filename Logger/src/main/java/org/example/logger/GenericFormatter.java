package org.example.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GenericFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date(record.getMillis()));
        return String.format("%s [%s] %s: %s%n", date, record.getLoggerName(), record.getLevel(), record.getMessage());
    }
}
