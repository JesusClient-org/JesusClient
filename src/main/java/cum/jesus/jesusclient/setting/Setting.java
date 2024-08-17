package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;

public abstract class Setting<T> {
    private String name;
    private T value;
    private T defaultValue;

    Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public abstract void addToBuilder(ConfigBuilder builder);

    public abstract void getFromReader(ConfigReader reader);
}
