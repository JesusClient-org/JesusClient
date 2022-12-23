package cum.jesus.jesusclient.scripting.runtime.events;

import cum.jesus.jesusclient.events.eventapi.types.EventType;

public class ScriptChatEvent {
    private EventType eventType;
    private String formattedMessage;
    private String unformattedMessage;

    public ScriptChatEvent(EventType eventType, String formattedMessage, String unformattedMessage) {
        this.eventType = eventType;
        this.formattedMessage = formattedMessage;
        this.unformattedMessage = unformattedMessage;
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

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public String getUnformattedMessage() {
        return unformattedMessage;
    }
}
