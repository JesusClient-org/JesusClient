package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.events.RenderAliveMobEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;

public class RevealHiddenMobs extends Module {
    public static BooleanSetting armorStands = new BooleanSetting("Armor stands too", false);

    public RevealHiddenMobs() {
        super("Reveal Entities", "Reveals invisible entities", Category.RENDER);
    }

    @EventTarget
    public void reveal(RenderAliveMobEvent<EntityLivingBase> event) {
        if (event.getEventType() != EventType.PRE) return;

        if (isToggled()) {
            EntityLivingBase entity = event.getEntity();

            if (entity.isInvisible()) {
                if (!armorStands.getObject() && entity instanceof EntityArmorStand) return;

                entity.setInvisible(false);
            }
        }
    }
}
