package cum.jesus.jesusclient.module.modules.dungeons;

import cum.jesus.jesusclient.events.PacketEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class CancelChestOpen extends Module {
    public CancelChestOpen() {
        super("Cancel Chest Open", "Cancels chest open packets", Category.DUNGEONS);
    }

    @EventTarget
    public void openWindow(PacketEvent event) {
        if (event.getEventType() == EventType.RECIEVE) {
            if (!SkyblockUtils.inDungeon || !(event.getPacket() instanceof S2DPacketOpenWindow)) return;

            if (((S2DPacketOpenWindow) event.getPacket()).getWindowTitle().getUnformattedText().equals("Chest") || ((S2DPacketOpenWindow) event.getPacket()).getWindowTitle().getUnformattedText().equals("Large Chest")) {
                event.setCancelled(true);

                mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(((S2DPacketOpenWindow) event.getPacket()).getWindowId()));
            }
        }
    }
}
