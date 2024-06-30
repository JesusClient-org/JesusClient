package cum.jesus.jesusclient.event;

public final class Priority {
    public static final byte
        HIGHEST = 0,
        HIGH = 1,
        MEDIUM = 2,
        LOW = 3,
        LOWEST = 4;

    public static final byte[] VALUES;

    static {
        VALUES = new byte[] {
                HIGHEST,
                HIGH,
                MEDIUM,
                LOW,
                LOWEST,
        };
    }
}
