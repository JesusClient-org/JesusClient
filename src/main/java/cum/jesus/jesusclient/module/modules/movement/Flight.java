package cum.jesus.jesusclient.module.modules.movement;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.MotionUpdateEvent;
import cum.jesus.jesusclient.events.WorldLoadEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight extends Module {
    public static Flight INSTANCE = new Flight();

    public NumberSetting<Double> flySpeed = new NumberSetting<>("Flight Speed", 1.0D, 0.1D, 5.0D);

    public Flight() {
        super("Flight", "Makes you fly", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (mc.thePlayer != null) mc.thePlayer.capabilities.isFlying = true;
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null) JesusClient.mc.thePlayer.capabilities.isFlying = false;
    }

    @EventTarget
    public void onUpdate(MotionUpdateEvent pre) {
        if (pre.getEventType() != EventType.PRE) return;
        if (isToggled()) {
            if(mc.gameSettings.keyBindJump.isPressed()) {
                mc.thePlayer.motionY += flySpeed.getObject()/10;
            }

            if(mc.gameSettings.keyBindSneak.isPressed()) {
                mc.thePlayer.motionY -= flySpeed.getObject()/10;
            }

            if(mc.gameSettings.keyBindForward.isPressed()) {
                mc.thePlayer.capabilities.setFlySpeed((float) (flySpeed.getObject()/10));
            }
        }
    }

    @EventTarget
    public void onWorldLoad(WorldLoadEvent event) {
        if (isToggled()) {
            toggle();

        }
    }
}
