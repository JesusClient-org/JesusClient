package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChatUtils {
    public static void sendMessage(Object message) {
        JesusClient.mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(message.toString()));
    }

    public static void sendPrefixMessage(Object message) {
        JesusClient.mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText("\u00A78[\u00A74Jesus Client\u00A78] \u00A77" + message));
    }

    /**
     * @param message the message to send in chat
     * @param action the command to run as a function
     * @param hoverString the string to show when chat message is hovered over
    */
    public static void sendClickableMessage(Object message, String action, String hoverString) {
        ChatComponentText comp = new ChatComponentText(message.toString());
        ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverString));
        comp.setChatStyle(style.setChatHoverEvent(hoverEvent));
        JesusClient.mc.thePlayer.addChatMessage(comp);
    }
}
