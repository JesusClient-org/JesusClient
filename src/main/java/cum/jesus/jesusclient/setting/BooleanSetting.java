package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.util.PrimitiveJesusSerializer;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public byte[] toBytes() {
        return PrimitiveJesusSerializer.serializeBoolean(getValue());
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        setValue(bytes[index++] != 0);
        return index;
    }
}
