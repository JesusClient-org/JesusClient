package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.events.RenderAliveMobEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;

public class PlayerScale extends Module {
    public static PlayerScale INSTANCE = new PlayerScale();

    public NumberSetting<Float> scaleX = new NumberSetting<>("Scale x", 1f, 0f, 15f);
    public NumberSetting<Float> scaleY = new NumberSetting<>("Scale y", 1f, 0f, 15f);
    public NumberSetting<Float> scaleZ = new NumberSetting<>("Scale z", 1f, 0f, 15f);

    public PlayerScale() {
        super("Player Scale", "Allows you to scale your player model", Category.RENDER);
    }

    @EventTarget
    public void renderSlimes(RenderAliveMobEvent<EntityPlayerSP> event) {
        if (event.getEventType() != EventType.PRE) return;
        if (isToggled() && event.getEntity() instanceof EntityPlayerSP) {
            EntityPlayerSP entity = (EntityPlayerSP) event.getEntity();

            if (entity == mc.thePlayer)
                GlStateManager.scale(PlayerScale.INSTANCE.scaleX.getObject(), PlayerScale.INSTANCE.scaleY.getObject(), PlayerScale.INSTANCE.scaleZ.getObject());
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
