package cum.jesus.jesusclient.script.trigger;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.script.ScriptManager;

public enum TriggerType {
    TICK,
    SECOND,

    EVENT,

    ;

    public void triggerAll(Object[] args) {
        ScriptManager.trigger(this, args);
    }

    public void triggerAll() {
        triggerAll(new Object[0]);
    }
}
