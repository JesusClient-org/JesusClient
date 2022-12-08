package cum.jesus.jesusclient.module.modules.self;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.GameTickEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.gui.clickgui.ClickGui;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Timer extends Module {
    public NumberSetting<Float> multiplier = new NumberSetting<>("Timer multiplier", 1.0f, 0.1f, 5.0f);

    public Timer() {
        super("Timer", "Changes the speed of the game", Category.SELF);
    }

    @EventTarget
    public void onTick(GameTickEvent e) {
        if (isToggled() && JesusClient.mc.currentScreen != (GuiScreen) ClickGui.INSTANCE) {
            (getTimer()).timerSpeed = multiplier.getObject();
        } else {
            resetTimer();
        }
    }

    public static net.minecraft.util.Timer getTimer() {
        return (net.minecraft.util.Timer) ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "timer", "field_71428_T" });
    }

    public static void resetTimer() {
        try {
            (getTimer()).timerSpeed = 1.0f;
        } catch (NullPointerException nullPointerException) {}
    }
}
