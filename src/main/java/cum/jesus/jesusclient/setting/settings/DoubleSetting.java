package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.INumberSetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.setting.Setting;

public final class DoubleSetting extends Setting<Double> implements INumberSetting {
    private final double min;
    private final double max;

    public DoubleSetting(String name, String description, double min, double max, double defaultValue, boolean hidden) {
        super(name, description, defaultValue, hidden);

        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, String description, double min, double max, double defaultValue) {
        this(name,description, min, max, defaultValue, false);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public double getNumber() {
        return getValue();
    }

    @Override
    public void setNumber(double value) {
        setValue(value);
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
        return 2;
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addDouble(getConfigName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(reader.getDouble(getConfigName(), getDefaultValue()));
    }
}
