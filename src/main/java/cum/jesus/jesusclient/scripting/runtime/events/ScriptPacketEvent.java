package cum.jesus.jesusclient.scripting.runtime.events;

import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.network.Packet;

public class ScriptPacketEvent {
    private final EventType eventType;
    private Packet packet;

    public ScriptPacketEvent(EventType eventType, Packet packet) {
        this.eventType = eventType;
        this.packet = packet;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean received() {
        return eventType == EventType.RECIEVE;
    }

    public boolean sent() {
        return eventType == EventType.SEND;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
