package cum.jesus.jesusclient.setting;

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

    private byte[] getBytes(long number, int size) {
        byte[] bytes = new byte[size];

        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ((number >> (8 * (size - 1 - i))) & 0xFF);
        }

        return bytes;
    }

    @Override
    public byte[] toBytes() {
        if (getValue() instanceof Integer) {
            return getBytes((Integer) getValue(), 4);
        } else if (getValue() instanceof Long) {
            return getBytes((Long) getValue(), 8);
        } else if (getValue() instanceof Short) {
            return getBytes((Short) getValue(), 2);
        } else if (getValue() instanceof Byte) {
            return getBytes((Byte) getValue(), 1);
        } else if (getValue() instanceof Float) {
            int intBits = Float.floatToIntBits((Float) getValue());
            return getBytes(intBits, 4);
        } else if (getValue() instanceof Double) {
            long longBits = Double.doubleToLongBits((Double) getValue());
            return getBytes(longBits, 8);
        }

        return new byte[0];
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        if (getValue() instanceof Integer) {
            int number = 0;
            for (int i = 0; i < 4; i++) {
                number |= ((int) bytes[index++] & 0xFF) << (24 - 8 * i);
            }
            setValue((T) Integer.valueOf(number));
        } else if (getValue() instanceof Long) {
            long number = 0;
            for (int i = 0; i < 8; i++) {
                number |= ((long) bytes[index++] & 0xFF) << (56 - 8 * i);
            }
            setValue((T) Long.valueOf(number));
        } else if (getValue() instanceof Short) {
            short number = 0;
            for (int i = 0; i < 2; i++) {
                number |= (short) (((short) bytes[index++] & 0xFF) << (8 - 8 * i));
            }
            setValue((T) Short.valueOf(number));
        } else if (getValue() instanceof Byte) {
            setValue((T) Byte.valueOf(bytes[index++]));
        } else if (getValue() instanceof Float) {
            int number = 0;
            for (int i = 0; i < 4; i++) {
                number |= ((int) bytes[index++] & 0xFF) << (24 - 8 * i);
            }
            setValue((T) Float.valueOf(Float.intBitsToFloat(number)));
        } else if (getValue() instanceof Double) {
            long number = 0;
            for (int i = 0; i < 8; i++) {
                number |= ((long) bytes[index++] & 0xFF) << (56 - 8 * i);
            }
            setValue((T) Double.valueOf(Double.longBitsToDouble(number)));
        }

        return index;
    }
}
