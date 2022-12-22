package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.callables.EventCancellable;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.network.Packet;

public class PacketEvent extends EventCancellable {
    private final EventType eventType;
    private Packet packet;

    public PacketEvent(EventType eventType, Packet packet) {
        this.eventType = eventType;
        this.packet = packet;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
