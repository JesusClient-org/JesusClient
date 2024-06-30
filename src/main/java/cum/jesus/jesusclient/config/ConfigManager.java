package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.file.JesusFile;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.setting.BooleanSetting;
import cum.jesus.jesusclient.setting.NumberSetting;
import cum.jesus.jesusclient.setting.Setting;
import cum.jesus.jesusclient.util.Logger;

import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {
    public void load() {
        List<String> issues = new ArrayList<>();

        if (JesusClient.instance.fileManager.hasFile("Client"))
            load("Client", JesusClient.instance.fileManager.get("Client"));

        JesusFile[] moduleFiles = JesusClient.instance.fileManager.getAllModuleFiles();

        for (JesusFile moduleFile : moduleFiles) {
            loadModule(moduleFile.getNameNoExt(), moduleFile, issues);
        }

        if (!issues.isEmpty()) {
            for (String issue : issues) {
                Logger.warn(issue);
            }
        }
    }

    public void save() {
        save("Client", JesusClient.instance.fileManager.get("Client"));

        for (Module module : JesusClient.instance.moduleHandler.getRegistry().getModules()) {
            String name = module.getName().replace(" ", "");
            save(name, JesusClient.instance.fileManager.get("modules/" + name));
        }
    }

    private void load(String settingName, JesusFile file) {
        byte[] bytes = file.read();
        int index = 0;

        for (Setting setting : JesusClient.instance.settingManager.getAllSettingsFrom(settingName)) {
            index = setting.fromBytes(bytes, index);
        }
    }

    private void loadModule(String settingName, JesusFile file, List<String> issues) {
        byte[] bytes = file.read();
        int index = 0;

        Module module = JesusClient.instance.moduleHandler.getModuleNoSpace(settingName);
        if (module == null) {
            issues.add("Module " + settingName + " does not exist.");
            return;
        }

        for (Setting setting : JesusClient.instance.settingManager.getAllSettingsFrom(settingName)) {
            index = setting.fromBytes(bytes, index);

            if (setting.getName().equals("toggled")) {
                if (setting instanceof BooleanSetting) {
                    module.setToggled(((BooleanSetting) setting).getValue());
                } else {
                    issues.add(settingName + ".toggled is not a boolean.");
                }
            } else if (setting.getName().equals("keybind")) {
                if (setting instanceof NumberSetting) {
                    module.setKeybind(((Number) setting.getValue()).intValue());
                } else {
                    issues.add(settingName + ".keybind is not a number.");
                }
            }
        }
    }

    private void save(String settingName, JesusFile file) {
        file.clear();
        for (Setting setting : JesusClient.instance.settingManager.getAllSettingsFrom(settingName)) {
            file.append(setting);
        }
    }
}
