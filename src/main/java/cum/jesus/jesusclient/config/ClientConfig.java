package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.setting.NumberSetting;
import cum.jesus.jesusclient.setting.StringSetting;

public final class ClientConfig {
    public StringSetting test = new StringSetting("test setting", "ambatablouuuu");
    public NumberSetting<Integer> number = new NumberSetting<>("test number", 10, 0, 100);
    public StringSetting string = new StringSetting("another string", "hello world");
}
