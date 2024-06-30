package cum.jesus.jesusclient.event.events.callables;

import cum.jesus.jesusclient.event.events.Cancellable;
import cum.jesus.jesusclient.event.events.Event;

public abstract class EventCancellable implements Event, Cancellable {
    private boolean cancelled;

    protected EventCancellable() {
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
