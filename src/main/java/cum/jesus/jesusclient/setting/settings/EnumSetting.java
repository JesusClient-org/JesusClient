package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.IEnumSetting;
import com.lukflug.panelstudio.setting.ILabeled;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.setting.Setting;

import java.util.Arrays;

public final class EnumSetting<E extends Enum<E>> extends Setting<E> implements IEnumSetting {
    private Class<E> elementClass;
    private ILabeled[] labeleds;

    public EnumSetting(String name, String description, E defaultValue, Class<E> elementClass, boolean hidden) {
        super(name, description, defaultValue, hidden);

        this.elementClass = elementClass;

        labeleds = Arrays.stream(elementClass.getEnumConstants()).map(value -> {
            return new ILabeled() {
                @Override
                public String getDisplayName() {
                    return value.toString();
                }
            };
        }).toArray(ILabeled[]::new);
    }

    public EnumSetting(String name, String description, E defaultValue, Class<E> elementClass) {
        this(name, description, defaultValue, elementClass, false);
    }

    @Override
    public void increment() {
        E[] array = elementClass.getEnumConstants();
        int index = getValue().ordinal() + 1;
        if (index >= array.length) index = 0;
        setValue(array[index]);
    }

    @Override
    public void decrement() {
        E[] array = elementClass.getEnumConstants();
        int index = getValue().ordinal() - 1;
        if (index < 0) index = array.length - 1;
        setValue(array[index]);
    }

    @Override
    public String getValueName() {
        return getValue().toString();
    }

    @Override
    public void setValueIndex(int index) {
        setValue(elementClass.getEnumConstants()[index]);
    }

    @Override
    public ILabeled[] getAllowedValues() {
        return labeleds;
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addInt(getConfigName(), getValue().ordinal());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValueIndex(reader.getInt(getConfigName(), getDefaultValue().ordinal()));
    }
}
