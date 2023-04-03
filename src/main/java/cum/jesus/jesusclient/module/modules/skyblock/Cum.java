package cum.jesus.jesusclient.module.modules.skyblock;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class Cum extends Module {
    private BooleanSetting cumInvMode = new BooleanSetting("Inv mode", false);
    private NumberSetting<Integer> cumMainSlot = new NumberSetting<>("Main Slot", 0, 0, 8);
    private BooleanSetting cumStash = new BooleanSetting("Pickup Stash", true);

    public Cum() {
        super("Cum", "Throws snowballs very fast", Category.DUNGEONS);
    }

    public void onEnable() {
        int oldSlot = JesusClient.mc.thePlayer.inventory.currentItem;
        if (!cumInvMode.getObject()) {
            for (int i = 0; i < 9; i++) {
                ItemStack a = JesusClient.mc.thePlayer.inventory.getStackInSlot(i);
                if (a != null && a.getDisplayName().toLowerCase().contains("snowball"))
                    Utils.throwSlot(i);
            }
        } else {
            int ballSlot = Utils.getAvailableHotbarSlot("Snowball");
            if (ballSlot == -1 || Utils.getAllSlots(ballSlot, "Snowball").size() == 0) {
                return;
            }
            Utils.throwSlot(ballSlot);
            for (Iterator<Integer> iterator = Utils.getAllSlots(ballSlot, "Snowball").iterator(); iterator.hasNext(); ) {
                int slotNum = ((Integer)iterator.next()).intValue();
                ItemStack curInSlot = JesusClient.mc.thePlayer.inventory.getStackInSlot(ballSlot);
                if (curInSlot == null)
                    JesusClient.mc.playerController.windowClick(JesusClient.mc.thePlayer.inventoryContainer.windowId, slotNum, ballSlot, 2, (EntityPlayer)JesusClient.mc.thePlayer);
                Utils.throwSlot(ballSlot);
            }
        }
        if (cumMainSlot.getObject() > 0 && cumMainSlot.getObject() <= 8) {
            JesusClient.mc.thePlayer.inventory.currentItem = cumMainSlot.getObject() - 1;
        } else {
            JesusClient.mc.thePlayer.inventory.currentItem = oldSlot;
        }
        if (cumStash.getObject())
            JesusClient.mc.thePlayer.sendChatMessage("/pickupstash");

        setToggled(false);
    }

    @Override
    public boolean shouldNotify() {
        return false;
    }
}
