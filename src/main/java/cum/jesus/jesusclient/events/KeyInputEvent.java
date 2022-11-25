package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.Event;

public class KeyInputEvent implements Event {
    private int key;

    public KeyInputEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}