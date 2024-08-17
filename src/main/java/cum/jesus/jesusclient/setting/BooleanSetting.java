package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addBoolean(getName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getBoolean(getName()));
    }
}