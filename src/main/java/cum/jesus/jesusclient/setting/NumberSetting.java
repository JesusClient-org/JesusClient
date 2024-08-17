package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;

import java.math.BigInteger;

public final class NumberSetting<T extends Number> extends Setting<T> {
    private T min;
    private T max;

    public NumberSetting(String name, T defaultValue, T min, T max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addNumber(getName(), getValue());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        if (getValue() instanceof Integer) {
            setValue((T) Integer.valueOf(reader.getInt(getName())));
        } else if (getValue() instanceof Float) {
            setValue((T) Float.valueOf(reader.getFloat(getName())));
        } else if (getValue() instanceof Long) {
            setValue((T) Long.valueOf(reader.getLong(getName())));
        } else if (getValue() instanceof Double) {
            setValue((T) Double.valueOf(reader.getDouble(getName())));
        } else if (getValue() instanceof Short) {
            setValue((T) Short.valueOf(reader.getShort(getName())));
        } else if (getValue() instanceof Byte) {
            setValue((T) Byte.valueOf(reader.getByte(getName())));
        } else if (getValue() instanceof BigInteger) {
            setValue((T) reader.getBigInt(getName()));
        } else {
            throw new RuntimeException("what the fuck kinda number is you using :sob:");
        }
    }
}
