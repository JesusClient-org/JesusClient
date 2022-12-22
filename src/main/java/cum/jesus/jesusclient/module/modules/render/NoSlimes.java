package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.events.RenderAliveMobEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;

public class NoSlimes extends Module {
    public static NoSlimes INSTANCE = new NoSlimes();

    public BooleanSetting homeOnly = new BooleanSetting("Private island only", true);

    public NoSlimes() {
        super("Remove Slimes", "Makes slimes not render (useful for slime minions)", Category.RENDER);
    }

    @EventTarget
    public void renderSlimes(RenderAliveMobEvent<EntityLivingBase> event) {
        if (event.getEventType() != EventType.PRE) return;
        if (isToggled()) {
            EntityLivingBase entity = event.getEntity();

            if (homeOnly.getObject() && !SkyblockUtils.onPrivateIsland) return;

            if (entity instanceof EntitySlime && SkyblockUtils.onSkyblock) {
                event.setCancelled(true);
            }
            if (SkyblockUtils.isNameStand(entity)) {
                event.setCancelled(true);
            }
        }
    }
}
