package cum.jesus.jesusclient.scripting.runtime.utils;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Console;
import jline.internal.Preconditions;

import java.io.PrintStream;

public class ScriptLogger {
    private static PrintStream output;

    public static PrintStream getOutput() {
        return output;
    }

    public static void setOutput(PrintStream out) {
        output = (PrintStream) Preconditions.checkNotNull(out);
    }

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

    private static void log(Level level, Object... messages) {
        synchronized(output) {
            output.format("[" + JesusClient.CLIENT_NAME + " (Script) | %s] ", level);
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
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " (Script) | " + Level.TRACE + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
        }
    }

    public static void debug(Object... messages) {
        if(JesusClient.devMode || System.getProperty("user.name").equals("Somer")) {
            log(Level.DEBUG, messages);
            if (Console.INSTANCE.isToggled()) {
                StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " (Script) | " + Level.DEBUG + "] ");

                for (Object message : messages) {
                    s.append(message).append(" ");
                }

                cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
            }

        }
    }

    public static void info(Object... messages) {
        log(Level.INFO, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " (Script) | " + Level.INFO + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
        }
    }

    public static void warn(Object... messages) {
        log(Level.WARN, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " (Script) | " + Level.WARN + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
        }
    }

    public static void error(Object... messages) {
        log(Level.ERROR, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " (Script) | " + Level.ERROR + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
        }
    }

    static {
        output = System.err; // so it's easier for me to see in intellij :D
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
