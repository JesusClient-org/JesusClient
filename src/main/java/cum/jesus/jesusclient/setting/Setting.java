package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.file.JesusSerializable;

public abstract class Setting<T> implements JesusSerializable {
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

    public abstract byte[] toBytes();

    /**
     * Convert .jesus data to setting
     * @param bytes Raw bytes from .jesus file
     * @param index The index of the bytes to begin from
     * @return The new index after reading
     */
    public abstract int fromBytes(byte[] bytes, int index);
}
