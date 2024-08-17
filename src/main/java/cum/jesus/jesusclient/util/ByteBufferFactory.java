package cum.jesus.jesusclient.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ByteBufferFactory {
    public static ByteBufferFactory heapByteBufferFactory() {
        return HeapByteBufferFactory.instance;
    }

    public abstract ByteBuffer newByteBuffer(int capacity);

    public static final class HeapByteBufferFactory extends ByteBufferFactory {
        public static final HeapByteBufferFactory instance = new HeapByteBufferFactory();

        @Override
        public ByteBuffer newByteBuffer(int capacity) {
            return ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
        }
    }
}
