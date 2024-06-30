package cum.jesus.jesusclient.event.events;

public abstract class EventStoppable implements Event {
    private boolean stopped;

    protected EventStoppable() {
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        stopped = true;
    }
}
