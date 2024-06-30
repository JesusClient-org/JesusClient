package cum.jesus.jesusclient.util;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.util.ChatComponentText;

public final class ChatUtils {
    public static void sendMessage(Object message, ChatColor color) {
        JesusClient.mc.thePlayer.addChatMessage(new ChatComponentText(color.code + message.toString()));
    }

    public static void sendMessage(Object message) {
        JesusClient.mc.thePlayer.addChatMessage(new ChatComponentText(message.toString()));
    }

    public static void sendPrefixMessage(Object message, ChatColor color) {
        JesusClient.mc.thePlayer.addChatMessage(new ChatComponentText("§8[§4Jesus Client§8] " + color.code + message.toString()));
    }

    public static void sendPrefixMessage(Object message) {
        sendPrefixMessage(message, ChatColor.GRAY);
    }
}
