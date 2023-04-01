package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Console;
import cum.jesus.jesusclient.module.modules.render.Gui;
import jline.internal.Preconditions;
import jline.internal.TestAccessible;
import net.minecraft.launchwrapper.Launch;
import org.lwjgl.Sys;

import java.awt.*;
import java.io.PrintStream;
import java.util.Arrays;

/**
* do not mind this
* i just didn't like how default log worked :troll:
* this just prints to the console
*/

public class Logger {
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
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " | " + Level.TRACE + "] ");

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
                StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " | " + Level.DEBUG + "] ");

                for (Object message : messages) {
                    s.append(message).append(" ");
                }

                cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false, Color.GREEN);
            }

        }
    }

    public static void info(Object... messages) {
        log(Logger.Level.INFO, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " | " + Level.INFO + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false);
        }
    }

    public static void warn(Object... messages) {
        log(Level.WARN, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " | " + Level.WARN + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false, new Color(255, 85, 85));
        }
    }

    public static void error(Object... messages) {
        log(Level.ERROR, messages);
        if (Console.INSTANCE.isToggled()) {
            StringBuilder s = new StringBuilder("[" + JesusClient.CLIENT_NAME + " | " + Level.ERROR + "] ");

            for (Object message : messages) {
                s.append(message).append(" ");
            }

            cum.jesus.jesusclient.gui.externalconsole.Console.INSTANCE.println(s.toString(), false, new Color(255, 85, 85));
        }
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