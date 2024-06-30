package cum.jesus.jesusclient.event.events.videogame;

import cum.jesus.jesusclient.event.events.Event;

public final class KeyInputEvent implements Event {
    private final int key;

    public KeyInputEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
