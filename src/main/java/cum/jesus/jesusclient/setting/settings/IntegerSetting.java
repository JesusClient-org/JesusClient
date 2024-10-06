package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.INumberSetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.setting.Setting;

public final class IntegerSetting extends Setting<Integer> implements INumberSetting {
    private final int min;
    private final int max;

    public IntegerSetting(String name, String description, int min, int max, int defaultValue, boolean hidden) {
        super(name, description, defaultValue, hidden);

        this.min = min;
        this.max = max;
    }

    public IntegerSetting(String name, String description, int min, int max, int defaultValue) {
        this(name,description, min, max, defaultValue, false);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public double getNumber() {
        return getValue();
    }

    @Override
    public void setNumber(double value) {
        setValue((int) Math.round(value));
    }

    @Override
    public double getMaximumValue() {
        return max;
    }

    @Override
    public double getMinimumValue() {
        return min;
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addInt(getConfigName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getInt(getConfigName(), getDefaultValue()));
    }
}
