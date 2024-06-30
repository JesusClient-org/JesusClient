package cum.jesus.jesusclient.event.events.callables;

import cum.jesus.jesusclient.event.EventType;
import cum.jesus.jesusclient.event.events.Event;
import cum.jesus.jesusclient.event.events.Typed;

public abstract class EventTyped implements Event, Typed {
    private final EventType eventType;

    protected EventTyped(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
