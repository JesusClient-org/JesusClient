package cum.jesus.jesusclient.scripting.runtime.events;

import cum.jesus.jesusclient.events.eventapi.types.EventType;

public class ScriptGameTickEvent {
    private EventType eventType;

    public ScriptGameTickEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean pre() {
        return eventType == EventType.PRE;
    }

    public boolean post() {
        return eventType == EventType.POST;
    }
}
