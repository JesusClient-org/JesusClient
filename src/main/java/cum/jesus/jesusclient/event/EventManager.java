package cum.jesus.jesusclient.event;

import cum.jesus.jesusclient.event.events.Event;
import cum.jesus.jesusclient.event.events.EventStoppable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager {
    private static final Map<Class<? extends Event>, List<MethodData>> REGISTRY = new HashMap<>();

    private EventManager() {
    }

    public static void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (isMethodBad(method)) {
                continue;
            }

            registerMethod(method, object);
        }
    }

    public static void unregister(Object object) {
        for (List<MethodData> dataList : REGISTRY.values()) {
            for (MethodData data : dataList) {
                if (data.source.equals(object)) {
                    dataList.remove(data);
                }
            }
        }

        cleanRegistry(true);
    }

    public static Event call(Event event) {
        List<MethodData> dataList = REGISTRY.get(event.getClass());

        if (dataList != null) {
            if (event instanceof EventStoppable) {
                EventStoppable stoppable = (EventStoppable) event;

                for (MethodData data : dataList) {
                    invoke(data, event);

                    if (stoppable.isStopped()) {
                        break;
                    }
                }
            } else {
                for (MethodData data : dataList) {
                    invoke(data, event);
                }
            }
        }

        return event;
    }

    private static void registerMethod(Method method, Object object) {
        Class<? extends Event> indexClass = (Class<? extends Event>) method.getParameterTypes()[0];
        MethodData data = new MethodData(object, method, method.getAnnotation(EventTarget.class).value());

        if (!data.target.isAccessible()) {
            data.target.setAccessible(true);
        }

        if (REGISTRY.containsKey(indexClass)) {
            if (!REGISTRY.get(indexClass).contains(data)) {
                REGISTRY.get(indexClass).add(data);
                sortListValue(indexClass);
            }
        } else {
            REGISTRY.put(indexClass, new CopyOnWriteArrayList<EventManager.MethodData>() {
                {
                    add(data);
                }
            });
        }
    }

    public static void cleanRegistry(boolean onlyEmptyEntries) {
        for (Iterator<Map.Entry<Class<? extends Event>, List<MethodData>>> it = REGISTRY.entrySet().iterator(); it.hasNext();) {
            if (!onlyEmptyEntries || it.next().getValue().isEmpty()) {
                it.remove();
            }
        }
    }

    private static void sortListValue(Class<? extends Event> indexClass) {
        List<MethodData> sortedList = new CopyOnWriteArrayList<>();

        for (byte priority : Priority.VALUES) {
            for (MethodData data : REGISTRY.get(indexClass)) {
                if (data.priority == priority) {
                    sortedList.add(data);
                }
            }
        }

        REGISTRY.put(indexClass, sortedList);
    }

    private static boolean isMethodBad(Method method) {
        return method.getParameterCount() != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    private static void invoke(MethodData data, Event argument) {
        try {
            data.target.invoke(data.source, argument);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }

    private static final class MethodData {
        private final Object source;
        private final Method target;
        private final byte priority;

        public MethodData(Object source, Method target, byte priority) {
            this.source = source;
            this.target = target;
            this.priority = priority;
        }
    }
}
