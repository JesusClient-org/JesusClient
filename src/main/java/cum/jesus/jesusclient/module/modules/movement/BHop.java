package cum.jesus.jesusclient.module.modules.movement;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.MotionUpdateEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.ModeSetting;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.utils.MovementUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BHop extends Module {
    public NumberSetting<Double> bHopSpeed = new NumberSetting<>("Speed", 1.0D, 0.1D, 10.0D);

    public ModeSetting bHopMode = new ModeSetting("BHop Mode", "1 block jump", new String[] { "1 block jump", "Vanilla jump" });

    public BHop() {
        super("BHop", "Makes you jump when moving", Category.MOVEMENT);
    }

    @EventTarget
    public void onUpdate(MotionUpdateEvent e) {
        if (e.getEventType() != EventType.PRE) return;
        //Log.info(!JesusClient.config.flight + " ," + Utils.isMoving() + " ," + !JesusClient.mc.thePlayer.isInWater() + " ," + JesusClient.config.bHop);
        if (!Flight.INSTANCE.isToggled() && MovementUtils.isMoving() && !JesusClient.mc.thePlayer.isInWater() && isToggled()) {
            double spd = 0.0025D * (bHopSpeed.getObject());
            KeyBinding.setKeyBindState(JesusClient.mc.gameSettings.keyBindJump.getKeyCode(), false);
            JesusClient.mc.thePlayer.noClip = true;
            if (JesusClient.mc.thePlayer.onGround) {
                switch (bHopMode.getObject()) {
                    case 0:
                        JesusClient.mc.thePlayer.motionY += 0.42f;
                        break;
                    case 1:
                        JesusClient.mc.thePlayer.jump();
                        break;
                }
            }
            JesusClient.mc.thePlayer.setSprinting(true);
            double h = (float)(Math.sqrt(JesusClient.mc.thePlayer.motionX * JesusClient.mc.thePlayer.motionX + JesusClient.mc.thePlayer.motionZ * JesusClient.mc.thePlayer.motionZ) + spd);
            MovementUtils.bop(h);
        }
    }
}
