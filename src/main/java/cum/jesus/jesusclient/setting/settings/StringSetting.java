package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.IStringSetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.setting.Setting;

/**
 * Represents a text field
 */
public final class StringSetting extends Setting<String> implements IStringSetting {
    public StringSetting(String name, String description, String defaultValue, boolean hidden) {
        super(name, description, defaultValue, hidden);
    }

    public StringSetting(String name, String description, String defaultValue) {
        super(name, description, defaultValue, false);
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addString(getConfigName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getString(getConfigName(), getDefaultValue()));
    }
}
