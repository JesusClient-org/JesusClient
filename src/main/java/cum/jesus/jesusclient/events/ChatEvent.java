package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.callables.EventCancellable;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.util.IChatComponent;

public class ChatEvent extends EventCancellable {
    private final EventType eventType;
    private IChatComponent message;
    private final byte type;

    public ChatEvent(EventType eventType, IChatComponent message, byte type) {
        this.eventType = eventType;
        this.message = message;
        this.type = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    public IChatComponent getMessage() {
        return message;
    }

    public byte getType() {
        return type;
    }
}
