package cum.jesus.jesusclient.scripting.runtime.deobfedutils;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.Logger;
import jline.internal.Log;

public class ScriptLogger {
    public static void trace(Object... messages) {
        Logger.trace("[Script] ", messages);
    }

    public static void debug(Object... messages) {
        Logger.debug("[Script] ", messages);
    }

    public static void info(Object... messages) {
        Logger.info("[Script] ", messages);
    }

    public static void warn(Object... messages) {
        Logger.warn("[Script] ", messages);
    }

    public static void error(Object... messages) {
        Logger.error("[Script] ", messages);
    }
}
