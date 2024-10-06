package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.IKeybindSetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.module.Keybind;
import cum.jesus.jesusclient.setting.Setting;

public final class KeybindSetting extends Setting<Keybind> implements IKeybindSetting {
    public KeybindSetting(String name, String description, Keybind defaultValue, boolean hidden) {
        super(name, description, defaultValue, hidden);
    }

    public KeybindSetting(String name, String description, Keybind defaultValue) {
        super(name, description, defaultValue, false);
    }

    public KeybindSetting(String name, String description, int key, boolean hidden) {
        this(name, description, new Keybind(key), hidden);
    }

    public KeybindSetting(String name, String description, int key) {
        this(name, description, new Keybind(key));
    }

    public KeybindSetting onPress(Runnable action) {
        getValue().setOnPress(action);
        return this;
    }

    @Override
    public int getKey() {
        return getValue().getKey();
    }

    @Override
    public void setKey(int key) {
        getValue().setKey(key);
    }

    @Override
    public String getKeyName() {
        return getValue().getKeyName();
    }

    @Override
    public void reset() {
        getValue().setKey(getDefaultValue().getKey());
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addInt(getConfigName(), getValue().getKey());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        getValue().setKey(reader.getInt(getConfigName(), getDefaultValue().getKey()));
    }
}
