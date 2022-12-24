package cum.jesus.jesusclient.scripting.runtime.jesusclient.utils;

import cum.jesus.jesusclient.utils.Logger;

public class WrapperLogger {
    public void trace(Object... messages) {
        Logger.trace(messages);
    }

    public void debug(Object... messages) {
        Logger.debug(messages);
    }

    public void info(Object... messages) {
        Logger.info(messages);
    }

    public void warn(Object... messages) {
        Logger.warn(messages);
    }

    public void error(Object... messages) {
        Logger.error(messages);
    }
}
