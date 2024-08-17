package cum.jesus.jesusclient.script.trigger;

import cum.jesus.jesusclient.event.EventTarget;
import cum.jesus.jesusclient.event.events.Event;
import cum.jesus.jesusclient.script.ScriptLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public final class EventTrigger extends Trigger {
    public static final EventListener EVENT_LISTENER = new EventListener();
    private static Map<Class<?>, SortedSet<EventTrigger>> eventTriggers = new HashMap<>();

    private Class<?> eventClass;

    public EventTrigger(Object method, Class<?> eventClass, ScriptLoader loader) {
        super(method, TriggerType.EVENT, loader);

        this.eventClass = eventClass;

        assert Event.class.isAssignableFrom(eventClass) : "EventTrigger expects an Event class";

        eventTriggers.computeIfAbsent(eventClass, (k) -> new TreeSet<>()).add(this);
    }

    public static void unregisterTriggers() {
        for (SortedSet<EventTrigger> value : eventTriggers.values()) {
            for (EventTrigger trigger : value) {
                trigger.unregister();
            }
        }
        eventTriggers.clear();
    }

    @Override
    public Trigger register() {
        eventTriggers.computeIfAbsent(eventClass, (k) -> new TreeSet<>()).add(this);
        return super.register();
    }

    @Override
    public Trigger unregister() {
        SortedSet<EventTrigger> triggers = eventTriggers.get(eventClass);
        if (triggers != null) {
            triggers.remove(this);
        }

        return super.unregister();
    }

    @Override
    public void trigger(Object[] args) {
        callMethod(args);
    }

    private static class EventListener {
        @EventTarget(cum.jesus.jesusclient.event.Priority.LOW)
        private void onEvent(Event event) {
            if (Thread.currentThread().getName().equals("Server thread"))
                return;

            SortedSet<EventTrigger> triggers = eventTriggers.get(event.getClass());
            if (triggers != null) {
                for (EventTrigger trigger : triggers) {
                    trigger.trigger(new Object[] { event });
                }
            }
        }
    }
}
