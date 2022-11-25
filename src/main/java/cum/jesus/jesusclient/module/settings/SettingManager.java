package cum.jesus.jesusclient.module.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingManager {
    @NotNull
    private HashMap<String, List<Setting>> valueMap = new HashMap<>();

    /**
     * @param name   The name of the owner
     * @param object The object where value-fields are declared
     */
    public void registerObject(String name, @NotNull Object object) {
        List<Setting> values = new ArrayList<>();
        for (final Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(object);

                if (obj instanceof Setting) {
                    values.add((Setting) obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        valueMap.put(name, values);
    }

    /**
     * @param name The name of the owner
     * @return If there's an owner with this name (the case is ignored) it will return all values of it else it will return null
     */
    @Nullable
    public List<Setting> getAllSettingsFrom(String name) {
        for (Map.Entry<String, List<Setting>> stringListEntry : valueMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    @NotNull
    public HashMap<String, List<Setting>> getAllValues() {
        return valueMap;
    }

    /**
     * @param owner The name of the owner
     * @param name  The name of the value
     * @return The value or null
     */
    @Nullable
    public Setting get(String owner, @NotNull String name) {
        List<Setting> found = getAllSettingsFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> name.equalsIgnoreCase(val.getName())).findFirst().orElse(null);
    }
}
