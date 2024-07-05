package cum.jesus.jesusclient.script.trigger;

import cum.jesus.jesusclient.script.ScriptLoader;
import org.jetbrains.annotations.NotNull;

public abstract class Trigger implements Comparable<Trigger> {
    private Object method;
    private TriggerType triggerType;

    protected ScriptLoader loader;

    private Priority priority = Priority.NORMAL;

    protected Trigger(Object method, TriggerType triggerType, ScriptLoader loader) {
        this.method = method;
        this.triggerType = triggerType;
        this.loader = loader;

        register();
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public Priority getPriority() {
        return priority;
    }

    public final Trigger setPriority(Priority priority) {
        this.priority = priority;

        unregister();
        register();

        return this;
    }

    public Trigger register() {
        loader.addTrigger(this);
        return this;
    }

    public Trigger unregister() {
        loader.removeTrigger(this);
        return this;
    }

    protected void callMethod(Object[] args) {
        loader.trigger(this, method, args);
    }

    public abstract void trigger(Object[] args);

    @Override
    public int compareTo(@NotNull Trigger other) {
        int ordCmp = priority.ordinal() - other.priority.ordinal();
        return ordCmp == 0 ? hashCode() - other.hashCode() : ordCmp;
    }

    public enum Priority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }
}
