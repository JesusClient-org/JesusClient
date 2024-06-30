package cum.jesus.jesusclient.event.events;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean state);
}
