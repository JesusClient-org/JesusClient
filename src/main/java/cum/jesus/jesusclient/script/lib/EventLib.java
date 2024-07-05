package cum.jesus.jesusclient.script.lib;

import cum.jesus.jesusclient.event.events.Cancellable;
import cum.jesus.jesusclient.event.events.Event;

public final class EventLib {
    private EventLib() {
    }

    public static void cancel(Object event) throws IllegalArgumentException {
        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        } else if (event instanceof Event) {
            throw new IllegalArgumentException("Attempt to cancel non-cancellable event " + event.getClass().getName());
        } else {
            throw new IllegalArgumentException("cancel() expects an event, but got " + event.getClass().getName());
        }
    }
}
