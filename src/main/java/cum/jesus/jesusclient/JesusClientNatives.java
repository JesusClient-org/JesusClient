package cum.jesus.jesusclient;

import cum.jesus.jesusclient.utils.Logger;

public class JesusClientNatives {
    public native void init();

    public native void stop();

    @Override
    protected void finalize() {
        Logger.trace("Native Class has been deleted");
    }

    public static class RT {
        public static void trace(String msg) {
            Logger.trace(msg);
        }

        public static void debug(String msg) {
            Logger.debug(msg);
        }

        public static void info(String msg) {
            Logger.info(msg);
        }

        public static void warn(String msg) {
            Logger.warn(msg);
        }

        public static void error(String msg) {
            Logger.error(msg);
        }
    }
}
