package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoBlind extends Module {
    public static NoBlind INSTANCE = new NoBlind();

    public BooleanSetting noFog = new BooleanSetting("Remove fog (includes blindness)", true);
    public BooleanSetting nausea = new BooleanSetting("Remove nausea effect", true);
    public BooleanSetting pumpkin = new BooleanSetting("Remove pumpkin", true);
    public BooleanSetting noFire = new BooleanSetting("Remove fire", false);


    public NoBlind() {
        super("NoBlind", "Disables annoying screen effects", Category.RENDER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void disableBlindness(EntityViewRenderEvent.FogDensity event) {
        if (!isToggled() || !noFog.getObject()) return;
        event.density = 0f;
        GlStateManager.setFogStart(998f);
        GlStateManager.setFogEnd(999f);
        event.setCanceled(true);
    }
}