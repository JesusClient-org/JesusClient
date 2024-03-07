package cum.jesus.jesusclient.setting;

public final class StringSetting extends Setting<String> {
    public StringSetting(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[getValue().length() + 1];

        for (int i = 0; i < getValue().length(); i++) {
            bytes[i] = (byte) getValue().charAt(i);
        }

        bytes[bytes.length - 1] = 0;

        return bytes;
    }

    @Override
    public int fromBytes(byte[] bytes, int index) {
        int length = 0;
        while (bytes[index + length] != 0) {
            length++;
        }

        setValue(new String(bytes, index, length));

        return index + length + 1;
    }
}
