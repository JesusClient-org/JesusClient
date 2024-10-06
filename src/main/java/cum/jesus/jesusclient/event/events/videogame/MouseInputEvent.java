package cum.jesus.jesusclient.event.events.videogame;

import cum.jesus.jesusclient.event.events.Event;

public final class MouseInputEvent implements Event {
    private final int button;

    public MouseInputEvent(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }
}
