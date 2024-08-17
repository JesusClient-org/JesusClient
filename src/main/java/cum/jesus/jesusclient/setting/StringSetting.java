package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;

public final class StringSetting extends Setting<String> {
    public StringSetting(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addString(getName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getString(getName()));
    }
}
