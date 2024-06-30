package cum.jesus.jesusclient.event.events.videogame;

import cum.jesus.jesusclient.event.EventType;
import cum.jesus.jesusclient.event.events.callables.EventTyped;

/**
 * Will get invoked on PRE and POST
 */
public final class GameTickEvent extends EventTyped {
    public GameTickEvent(EventType eventType) {
        super(eventType);
    }
}
