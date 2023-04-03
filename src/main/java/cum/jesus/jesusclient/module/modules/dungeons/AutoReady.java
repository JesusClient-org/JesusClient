package cum.jesus.jesusclient.module.modules.dungeons;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.ChatEvent;
import cum.jesus.jesusclient.events.GameTickEvent;
import cum.jesus.jesusclient.events.WorldLoadEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import java.util.List;

public class AutoReady extends Module {
    public AutoReady() {
        super("Auto Ready", "Automatically readies up in dungeons", Category.SKYBLOCK);
    }

    private static boolean readyUp = false;
    private static boolean dungeonStarted = false;

    @EventTarget
    public void tick(GameTickEvent event) {
        if (event.getEventType() != EventType.PRE) return;

        if (isToggled() && SkyblockUtils.inDungeon && !dungeonStarted) {
            if (!readyUp) {
                for (Entity entity : mc.theWorld.getLoadedEntityList()) {
                    if (entity instanceof EntityArmorStand) {
                        if (entity.hasCustomName() && entity.getCustomNameTag().equals("§bMort")) {
                            List<Entity> possibleEntities = entity.getEntityWorld().getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(0, 3, 0), e -> e instanceof EntityPlayer);
                            if (possibleEntities.isEmpty()) {
                                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, possibleEntities.get(0));
                                readyUp = true;
                            }
                        }
                    }
                }
            }

            String chestName = Utils.getInventoryName();
            if (readyUp && chestName != null) {
                if (chestName.equals("Start Dungeon?")) {
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 13, 2, 0, mc.thePlayer);
                    return;
                }

                if (chestName.startsWith("Catacombs - ")) {
                    for (Slot slot : mc.thePlayer.openContainer.inventorySlots) {
                        if (slot.getStack() != null && slot.getStack().getDisplayName().contains(JesusClient.username)) {
                            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, slot.slotNumber, 2, 0, mc.thePlayer);
                            mc.thePlayer.closeScreen();
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void chat(ChatEvent event) {
        if (event.getEventType() != EventType.RECIEVE) return;

        if (!dungeonStarted && event.getMessage().getUnformattedText().contains("Dungeon starts in 3 seconds.")) {
            dungeonStarted = true;
        }
    }

    @EventTarget
    public void worldLoad(WorldLoadEvent event) {
        readyUp = false;
        dungeonStarted = false;
    }
}
