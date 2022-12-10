package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import jline.internal.Preconditions;
import jline.internal.TestAccessible;
import net.minecraft.launchwrapper.Launch;

import java.io.PrintStream;

/*
do not mind this
i just didn't like how default log worked :troll:
this just prints to the console
*/

public final class Logger {
    public Logger() {

    }

    private static PrintStream output;

    public static PrintStream getOutput() {
        return output;
    }

    public static void setOutput(PrintStream out) {
        output = (PrintStream) Preconditions.checkNotNull(out);
    }

    @TestAccessible
    private static void render(PrintStream out, Object message) {
        if (message.getClass().isArray()) {
            Object[] array = (Object[])((Object[])message);
            out.print("[");

            for(int i = 0; i < array.length; ++i) {
                out.print(array[i]);
                if (i + 1 < array.length) {
                    out.print(",");
                }
            }

            out.print("]");
        } else {
            out.print(message);
        }
    }

    @TestAccessible
    private static void log(Logger.Level level, Object... messages) {
        synchronized(output) {
            output.format("[" + JesusClient.CLIENT_NAME + " | %s] ", level);
            for(int i = 0; i < messages.length; ++i) {
                if (i + 1 == messages.length && messages[i] instanceof Throwable) {
                    output.println();
                    ((Throwable)messages[i]).printStackTrace(output);
                } else {
                    render(output, messages[i]);
                }
            }
            output.println();
            output.flush();
        }
    }

    public static void trace(Object... messages) {
        log(Level.TRACE, messages);
    }

    public static void debug(Object... messages) {
        if(JesusClient.devMode) {
            log(Level.DEBUG, messages);
        }
    }

    public static void info(Object... messages) {
        log(Logger.Level.INFO, messages);
    }

    public static void warn(Object... messages) {
        log(Level.WARN, messages);
    }

    public static void error(Object... messages) {
        log(Level.ERROR, messages);
    }

    static {
        output = System.err;
    }

    public static enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;

        private Level() {
        }
    }
}