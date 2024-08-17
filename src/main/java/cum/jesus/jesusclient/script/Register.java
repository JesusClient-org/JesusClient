package cum.jesus.jesusclient.script;

import cum.jesus.jesusclient.script.trigger.BasicTrigger;
import cum.jesus.jesusclient.script.trigger.EventTrigger;
import cum.jesus.jesusclient.script.trigger.Trigger;
import cum.jesus.jesusclient.script.trigger.TriggerType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Register {
    private static Map<String, Method> methodMap = new HashMap<>();

    public abstract ScriptLoader getLoader();

    public Trigger register(Object triggerType, Object method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (triggerType instanceof Class<?>) {
            return new EventTrigger(method, (Class<?>) triggerType, getLoader());
        }

        assert triggerType instanceof String : "register() requires a string or event class as its argument";

        Method meth = methodMap.get(triggerType);
        if (meth == null) {
            String name = ((String) triggerType).toLowerCase();

            for (Method m : Register.class.getDeclaredMethods()) {
                if (m.getName().equalsIgnoreCase("register" + name)) {
                    meth = m;
                    break;
                }
            }
        }

        if (meth == null) {
            throw new NoSuchMethodException("No trigger type named " + triggerType);
        }

        return (Trigger) meth.invoke(this, method);
    }

    /**
     * 1 argument - elapsed ticks
     */
    public BasicTrigger registerTick(Object method) {
        return new BasicTrigger(method, TriggerType.TICK, getLoader());
    }

    /**
     * 1 argument - elapsed seconds
     */
    public BasicTrigger registerSecond(Object method) {
        return new BasicTrigger(method, TriggerType.SECOND, getLoader());
    }
}
