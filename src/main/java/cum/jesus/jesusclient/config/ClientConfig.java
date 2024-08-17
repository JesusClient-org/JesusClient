package cum.jesus.jesusclient.config;

import cum.jesus.jesusclient.file.builder.FileBuilder;
import cum.jesus.jesusclient.file.reader.FileReader;
import cum.jesus.jesusclient.setting.Setting;
import cum.jesus.jesusclient.setting.StringSetting;

import java.util.Arrays;
import java.util.List;

public final class ClientConfig implements IConfigurable {
    public StringSetting commandPrefix = new StringSetting("Command prefix", "-");

    @Override
    public String getName() {
        return "Client";
    }

    @Override
    public String getFileName() {
        return "Client";
    }

    @Override
    public List<Setting> getSettings() {
        return Arrays.asList(commandPrefix);
    }

    @Override
    public void writeSpecial(FileBuilder builder) {

    }

    @Override
    public void readSpecial(FileReader reader) {

    }
}
