package cum.jesus.jesusclient.setting;

import cum.jesus.jesusclient.util.PrimitiveJesusSerializer;

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
    public byte[] toBytes() {
        if (getValue() instanceof Integer) {
            return PrimitiveJesusSerializer.serializeInt(getValue().intValue());
        } else if (getValue() instanceof Long) {
            return PrimitiveJesusSerializer.serializeLong(getValue().longValue());
        } else if (getValue() instanceof Short) {
            return PrimitiveJesusSerializer.serializeShort(getValue().shortValue());
        } else if (getValue() instanceof Byte) {
            return PrimitiveJesusSerializer.serializeByte(getValue().byteValue());
        } else if (getValue() instanceof Float) {
            return PrimitiveJesusSerializer.serializeFloat(getValue().floatValue());
        } else if (getValue() instanceof Double) {
            return PrimitiveJesusSerializer.serializeDouble(getValue().doubleValue());
        }

        return new byte[0];
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        if (getValue() instanceof Integer) {
            int[] a = new int[] { getValue().intValue() };
            index = PrimitiveJesusSerializer.deserializeInt(bytes, index, a);
            setValue((T) Integer.valueOf(a[0]));
        } else if (getValue() instanceof Long) {
            long[] a = new long[] { getValue().longValue() };
            index = PrimitiveJesusSerializer.deserializeLong(bytes, index, a);
            setValue((T) Long.valueOf(a[0]));
        } else if (getValue() instanceof Short) {
            short[] a = new short[] { getValue().shortValue() };
            index = PrimitiveJesusSerializer.deserializeShort(bytes, index, a);
            setValue((T) Short.valueOf(a[0]));
        } else if (getValue() instanceof Byte) {
            setValue((T) Byte.valueOf(bytes[index++]));
        } else if (getValue() instanceof Float) {
            float[] a = new float[] { getValue().floatValue() };
            index = PrimitiveJesusSerializer.deserializeFloat(bytes, index, a);
            setValue((T) Float.valueOf(a[0]));
        } else if (getValue() instanceof Double) {
            double[] a = new double[] { getValue().doubleValue() };
            index = PrimitiveJesusSerializer.deserializeDouble(bytes, index, a);
            setValue((T) Double.valueOf(a[0]));
        }

        return index;
    }
}
