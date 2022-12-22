package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.Event;
import cum.jesus.jesusclient.events.eventapi.types.EventType;

public class GameTickEvent implements Event {
    private final EventType eventType;

    public GameTickEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
