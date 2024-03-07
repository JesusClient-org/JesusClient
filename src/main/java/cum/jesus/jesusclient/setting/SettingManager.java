package cum.jesus.jesusclient.setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SettingManager {
    private Map<String, List<Setting>> settingMap = new HashMap<>();

    public void registerObject(String name, Object obj) {
        List<Setting> settings = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object object = field.get(obj);

                if (object instanceof Setting) {
                    settings.add((Setting) object);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        settingMap.put(name.toLowerCase(), settings);
    }

    public List<Setting> getAllSettingsFrom(String name) {
        return settingMap.getOrDefault(name.toLowerCase(), null);
    }

    public Setting get(String owner, String name) {
        List<Setting> found = getAllSettingsFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> name.equalsIgnoreCase(val.getName())).findFirst().orElse(null);
    }
}
