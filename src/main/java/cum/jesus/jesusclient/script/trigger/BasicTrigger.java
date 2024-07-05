package cum.jesus.jesusclient.script.trigger;

import cum.jesus.jesusclient.script.ScriptLoader;

public final class BasicTrigger extends Trigger {
    public BasicTrigger(Object method, TriggerType type, ScriptLoader loader) {
        super(method, type, loader);
    }

    @Override
    public void trigger(Object[] args) {
        callMethod(args);
    }
}
