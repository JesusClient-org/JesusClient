package cum.jesus.jesusclient.util;

import cum.jesus.jesusclient.file.JesusSerializable;

import java.nio.charset.StandardCharsets;

public final class PrimitiveJesusSerializer {
    public static byte[] serializeByte(byte b) {
        return new NumberSerializable(b, 1).toBytes();
    }

    public static byte[] serializeShort(short s) {
        return new NumberSerializable(s, 2).toBytes();
    }

    public static byte[] serializeInt(int i) {
        return new NumberSerializable(i, 4).toBytes();
    }

    public static byte[] serializeLong(long l) {
        return new NumberSerializable(l, 8).toBytes();
    }

    public static byte[] serializeFloat(float f) {
        return new NumberSerializable(Float.floatToIntBits(f), 4).toBytes();
    }

    public static byte[] serializeDouble(double d) {
        return new NumberSerializable(Double.doubleToLongBits(d), 8).toBytes();
    }

    public static byte[] serializeChar(char c) {
        return String.valueOf(c).getBytes(StandardCharsets.UTF_8);
    }

    private static final class NumberSerializable implements JesusSerializable {
        private long number;
        private int size;

        public NumberSerializable(long number, int size) {
            this.number = number;
            this.size = size;
        }

        @Override
        public byte[] toBytes() {
            byte[] bytes = new byte[size];

            for (int i = 0; i < size; i++) {
                bytes[i] = (byte) ((number >> (8 * (size - 1 - i))) & 0xFF);
            }

            return bytes;
        }

        @Override
        public int fromBytes(byte[] bytes, int index) {
            long number = 0;

            for (int i = 0; i < size; i++) {
                number |= ((long) bytes[index++] & 0xFF) << (8 * (size - 1 - i));
            }

            return index;
        }
    }
}
