package cum.jesus.jesusclient.scripting.runtime.jesusclient;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.scripting.runtime.jesusclient.utils.WrapperChatUtils;
import cum.jesus.jesusclient.scripting.runtime.jesusclient.utils.WrapperHttpUtils;
import cum.jesus.jesusclient.scripting.runtime.jesusclient.utils.WrapperLogger;

public class WrapperJesusClient {
    public static WrapperJesusClient getJesusClient() {
        return new WrapperJesusClient();
    }

    public WrapperLogger getLogger() {
        return new WrapperLogger();
    }

    public WrapperChatUtils getChatUtils() {
        return new WrapperChatUtils();
    }

    public WrapperHttpUtils getHttpUtils() {
        return new WrapperHttpUtils();
    }

    public void activateDevMode() {
        JesusClient.devMode = true;
    }
}
