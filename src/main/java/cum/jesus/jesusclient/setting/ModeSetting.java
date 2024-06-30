package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.util.PrimitiveJesusSerializer;

public final class ModeSetting extends Setting<Integer> {
    private String[] modes;

    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, 0);
        this.modes = modes;

        setValue(defaultValue);
    }

    public String[] getModes() {
        return modes;
    }

    public void setValue(String str) {
        int value = -1;

        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];
            if (mode.equalsIgnoreCase(str)) value = i;
        }

        if (value == -1) throw new IllegalArgumentException("Value " + str + " wasn't found.");

        setValue(value);
    }

    @Override
    public void setValue(Integer value) {
        if (value < 0 || modes.length <= value)
            throw new IllegalArgumentException(value + " is not valid (max: " + (modes.length - 1) + ")");

        super.setValue(value);
    }

    @Override
    public byte[] toBytes() {
        return PrimitiveJesusSerializer.serializeInt(getValue());
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        int[] value = new int[1];
        index = PrimitiveJesusSerializer.deserializeInt(bytes, index, value);
        setValue(value[0]);
        return index;
    }
}
