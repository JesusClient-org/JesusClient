package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.util.PrimitiveJesusSerializer;

public final class StringSetting extends Setting<String> {
    public StringSetting(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public byte[] toBytes() {
        return PrimitiveJesusSerializer.serializeString(getValue());
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        String[] tmp = new String[1];
        index = PrimitiveJesusSerializer.deserializeString(bytes, index, tmp);
        setValue(tmp[0]);
        return index;
    }
}
