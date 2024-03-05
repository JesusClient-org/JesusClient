package cum.jesus.jesusclient.util;

import java.io.PrintStream;

public final class Logger {
    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private static PrintStream stream = System.out;
    private static PrintStream errorStream = System.err;

    public static PrintStream getStream() {
        return stream;
    }

    public static void setStream(PrintStream stream) {
        Logger.stream = stream;
    }

    public static PrintStream getErrorStream() {
        return errorStream;
    }

    public static void setErrorStream(PrintStream errorStream) {
        Logger.errorStream = errorStream;
    }

    private static void render(final PrintStream out, final Object msg) {
        if (msg.getClass().isArray()) {
            Object[] array = (Object[]) msg;

            out.print('[');
            for (int i = 0; i < array.length; i++) {
                out.print(array[i]);
                if (i + 1 < array.length) {
                    out.print(", ");
                }
            }
            out.print(']');
        } else {
            out.print(msg);
        }
    }

    public static void log(final Level level, final Object... msg) {
        final PrintStream out;

        if (level == Level.WARN || level == Level.ERROR) {
            out = errorStream;
        } else {
            out = stream;
        }

        synchronized (out) {
            out.format("[JesusClient@%s/%s] ", Thread.currentThread().getName(), level);
            for (int i = 0; i < msg.length; i++) {
                if (i + 1 == msg.length && msg[i] instanceof Throwable) {
                    out.println();
                    ((Throwable)msg[i]).printStackTrace(out);
                }
                else {
                    render(out, msg[i]);
                }
            }

            out.println();
            out.flush();
        }
    }

    public static void trace(final Object... msg) {
        log(Level.TRACE, msg);
    }

    public static void debug(final Object... msg) {
        log(Level.DEBUG, msg);
    }

    public static void info(final Object... msg) {
        log(Level.INFO, msg);
    }

    public static void warn(final Object... msg) {
        log(Level.WARN, msg);
    }

    public static void error(final Object... msg) {
        log(Level.ERROR, msg);
    }
}
