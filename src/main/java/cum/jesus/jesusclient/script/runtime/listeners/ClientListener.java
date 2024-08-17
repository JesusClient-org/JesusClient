package cum.jesus.jesusclient.script.runtime.listeners;

import cum.jesus.jesusclient.event.EventTarget;
import cum.jesus.jesusclient.event.EventType;
import cum.jesus.jesusclient.event.Priority;
import cum.jesus.jesusclient.event.events.Event;
import cum.jesus.jesusclient.event.events.videogame.GameTickEvent;
import cum.jesus.jesusclient.script.trigger.TriggerType;
import cum.jesus.jesusclient.util.Logger;

public final class ClientListener {
    public static final ClientListener INSTANCE = new ClientListener();

    private int ticksPassed; // will last about 1200 days. please do not afk for too long
    private int secondsPassed; // will last a long ass time

    private ClientListener() {
        ticksPassed = 0;
        secondsPassed = 0;
    }

    @EventTarget(Priority.LOW) // we want scripts to usually happen after everything else
    private void onTick(GameTickEvent event) {
        if (event.getEventType() != EventType.POST) return;

        TriggerType.TICK.triggerAll(new Object[] { ticksPassed++ });

        if (ticksPassed % 20 == 0) {
            TriggerType.SECOND.triggerAll(new Object[] { secondsPassed++ });
        }
    }
}
