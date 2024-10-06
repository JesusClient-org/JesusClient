package cum.jesus.jesusclient.util;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.event.EventTarget;
import cum.jesus.jesusclient.event.EventType;
import cum.jesus.jesusclient.event.events.videogame.GameTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public final class Utils {
    public static final Utils INSTANCE = new Utils();
    public static final Minecraft mc = JesusClient.mc;

    private static GuiScreen display = null;

    private Utils() {
    }

    public static void displayGuiScreen(GuiScreen guiScreen) {
        display = guiScreen;
    }

    @EventTarget
    private void onTick(GameTickEvent event) {
        if (event.getEventType() != EventType.PRE) return;

        if (display != null) {
            try {
                mc.displayGuiScreen(display);
            } catch (Exception e) {
                e.printStackTrace();
            }

            display = null;
        }
    }
}
