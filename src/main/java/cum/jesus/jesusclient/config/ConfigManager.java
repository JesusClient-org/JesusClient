package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.file.JesusFile;
import cum.jesus.jesusclient.setting.Setting;

public final class ConfigManager {
    public void load(JesusFile file) {
        byte[] bytes = file.read();
        int index = 0;

        for (Setting setting : JesusClient.instance.settingManager.getAllSettingsFrom(file.getNameNoExt())) {
            index = setting.fromBytes(bytes, index);
        }
    }

    public void save(JesusFile file) {
        file.clear();
        for (Setting setting : JesusClient.instance.settingManager.getAllSettingsFrom(file.getNameNoExt())) {
            file.append(setting);
        }
    }
}
