package cum.jesus.jesusclient.utils;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class PacketShit {
    public static ArrayList<Packet<?>> noEvent = new ArrayList<>();

    public static void sendPacketNoEvent(Packet<?> packet) {
        noEvent.add(packet);
        JesusClient.mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
