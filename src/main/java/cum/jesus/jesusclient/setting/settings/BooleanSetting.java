package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.IBooleanSetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.setting.Setting;

/**
 * Represents a switch or checkbox
 */
public final class BooleanSetting extends Setting<Boolean> implements IBooleanSetting {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue, false);
    }

    public BooleanSetting(String name, String description, boolean defaultValue, boolean hidden) {
        super(name, description, defaultValue, hidden);
    }

    @Override
    public void toggle() {
        setValue(!getValue());
    }

    @Override
    public boolean isOn() {
        return getValue();
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addBoolean(getConfigName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getBoolean(getConfigName(), getDefaultValue()));
    }
}