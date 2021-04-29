package server.model;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LogAppender extends AppenderSkeleton {
    private String logs;

    public LogAppender() { logs = ""; }

    protected void append(LoggingEvent event) {
        logs += event.getLoggerName() + " " +
                event.getMessage() + "\n";
    }

    public void close() { logs = ""; }

    public boolean requiresLayout() { return false; }

    public String getLogs() { return logs; }
}
