package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class ScriptModule2 extends Module {
    private final List<Setting> settings = new ArrayList<>();

    public ScriptModule2(String name, String description, Category category) {
        super(name, description, category);
    }

    public ScriptModule2(String name, String description, Category category, boolean canBeEnabled, boolean hidden, int keybind) {
        super(name, description, category, canBeEnabled, hidden, keybind);
    }

    public void setScriptName(String scriptName) {
        name = "(" + scriptName + ") " + name;
    }

    protected void addSettings() {

    }

    public void doSettings() {
        JesusClient.INSTANCE.settingManager.registerObject(getName(), this);
        addSettings();
        for (Setting setting : settings) {
            setting.setPremiumOnly(true);
            JesusClient.INSTANCE.settingManager.getAllSettingsFrom(getName()).add(setting);
        }
    }

    protected void addSetting(Setting setting) {
        settings.add(setting);
    }
}
