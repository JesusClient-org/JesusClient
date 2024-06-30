package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.config.IConfigurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SettingManager {
    private Map<String, List<Setting>> settingMap = new HashMap<>();

    public void registerObject(String name, IConfigurable obj) {
        settingMap.put(name.toLowerCase(), obj.getSettings());
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
