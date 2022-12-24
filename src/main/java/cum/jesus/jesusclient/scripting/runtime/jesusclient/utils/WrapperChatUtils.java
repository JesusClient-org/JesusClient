package cum.jesus.jesusclient.scripting.runtime.jesusclient.utils;

import cum.jesus.jesusclient.utils.ChatUtils;

public class WrapperChatUtils {
    public void sendChatMessage(Object message) {
        ChatUtils.sendMessage(message);
    }

    public void sendPrefixMessage(Object message) {
        ChatUtils.sendPrefixMessage(message);
    }

    public void sendClickableMessage(Object message, String action, String hoverString) {
        ChatUtils.sendClickableMessage(message, action, hoverString);
    }
}
