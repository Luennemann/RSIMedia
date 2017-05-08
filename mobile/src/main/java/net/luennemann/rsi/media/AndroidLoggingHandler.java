package net.luennemann.rsi.media;

/**
 * Created by patrick on 07.05.2017.
 */
import android.util.Log;
import java.util.logging.*;

/**
 * Make JUL work on Android.
 */
public class AndroidLoggingHandler extends Handler {

    public static void reset(Handler rootHandler) {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        rootLogger.addHandler(rootHandler);
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord record) {
        if (!super.isLoggable(record))
            return;

        String name = record.getLoggerName();
        int maxLength = 30;
        String tag = name.length() > maxLength ? name.substring(name.length() - maxLength) : name;

        try {
            int value = record.getLevel().intValue();
            int level = Log.DEBUG;
            if (value >= 1000) {
                level = Log.ERROR;
            } else if (value >= 900) {
                level = Log.WARN;
            } else if (value >= 800) {
                level = Log.INFO;
            } else {
                level = Log.DEBUG;
            }
            Log.println(level, tag, record.getMessage());
            if (record.getThrown() != null) {
                Log.println(level, tag, Log.getStackTraceString(record.getThrown()));
            }
        } catch (RuntimeException e) {
            Log.e("AndroidLoggingHandler", "Error logging message.", e);
        }
    }
}
