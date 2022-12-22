package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.callables.EventCancellable;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.util.IChatComponent;

public class ChatEvent extends EventCancellable {
    private final EventType eventType;
    private IChatComponent message;

    public ChatEvent(EventType eventType, IChatComponent message) {
        this.eventType = eventType;
        this.message = message;
    }

    public EventType getEventType() {
        return eventType;
    }

    public IChatComponent getMessage() {
        return message;
    }
}
