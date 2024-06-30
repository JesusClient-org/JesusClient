package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.setting.Setting;
import cum.jesus.jesusclient.setting.StringSetting;

import java.util.Arrays;
import java.util.List;

public final class ClientConfig implements IConfigurable {
    public StringSetting commandPrefix = new StringSetting("Command prefix", "-");

    @Override
    public List<Setting> getSettings() {
        return Arrays.asList(commandPrefix);
    }
}
