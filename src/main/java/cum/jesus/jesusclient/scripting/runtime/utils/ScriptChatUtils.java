package cum.jesus.jesusclient.scripting.runtime.utils;

import cum.jesus.jesusclient.utils.ChatUtils;

public class ScriptChatUtils {
    public static void sendMessage(Object message) {
        ChatUtils.sendMessage(message);
    }

    public static void sendPrefixMessage(Object message) {
        ChatUtils.sendPrefixMessage(message);
    }

    /**
     * @param message the message to send in chat
     * @param action the command to run as a function
     * @param hoverString the string to show when chat message is hovered over
     */
    public static void sendClickableMessage(Object message, String action, String hoverString) {
        ChatUtils.sendClickableMessage(message, action, hoverString);
    }
}
