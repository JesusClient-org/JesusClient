package cum.jesus.jesusclient.util;

import cum.jesus.jesusclient.file.JesusSerializable;

public final class PrimitiveJesusSerializer {
    public static byte[] serializeBoolean(boolean b) {
        return new byte[] { (byte) (b ? 1 : 0) };
    }

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
        return new NumberSerializable(c, 2).toBytes();
    }

    public static byte[] serializeString(String s) {
        return new StringSerializable(s).toBytes();
    }

    public static int deserializeBoolean(byte[] bytes, int index, boolean[] valueHolder) {
        valueHolder[0] = bytes[index++] != 0;
        return index;
    }

    public static int deserializeByte(byte[] bytes, int index, byte[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 1);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = (byte) ns.number;
        return index;
    }

    public static int deserializeShort(byte[] bytes, int index, short[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 2);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = (short) ns.number;
        return index;
    }

    public static int deserializeInt(byte[] bytes, int index, int[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 4);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = (int) ns.number;
        return index;
    }

    public static int deserializeLong(byte[] bytes, int index, long[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 8);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = ns.number;
        return index;
    }

    public static int deserializeFloat(byte[] bytes, int index, float[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 4);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = Float.intBitsToFloat((int) ns.number);
        return index;
    }

    public static int deserializeDouble(byte[] bytes, int index, double[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 8);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = Double.longBitsToDouble(ns.number);
        return index;
    }

    public static int deserializeChar(byte[] bytes, int index, char[] valueHolder) {
        NumberSerializable ns = new NumberSerializable(0, 2);
        index = ns.fromBytes(bytes, index);
        valueHolder[0] = (char) ns.number;
        return index;
    }

    public static int deserializeString(byte[] bytes, int index, String[] valueHolder) {
        StringSerializable ss = new StringSerializable(null);
        index = ss.fromBytes(bytes, index);
        valueHolder[0] = ss.string;
        return index;
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

    private static final class StringSerializable implements JesusSerializable {
        private String string;

        public StringSerializable(String string) {
            this.string = string;
        }

        @Override
        public byte[] toBytes() {
            byte[] bytes = new byte[string.length() + 1];

            for (int i = 0; i < string.length(); i++) {
                bytes[i] = (byte) string.charAt(i);
            }

            bytes[bytes.length - 1] = 0;

            return bytes;
        }

        @Override
        public int fromBytes(byte[] bytes, int index) {
            int length = 0;

            while(bytes[index + length] != 0) {
                length++;
            }

            string = new String(bytes, index, length);

            return index + length + 1;
        }
    }
}
