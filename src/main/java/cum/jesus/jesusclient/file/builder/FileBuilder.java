package cum.jesus.jesusclient.file.builder;

import cum.jesus.jesusclient.util.ByteBufferFactory;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileBuilder {
    private ByteBuffer bb;
    private final ByteBufferFactory byteBufferFactory;

    private boolean nested = false;
    private boolean finished = false;

    private static final int DEFAULT_BUFFER_SIZE = 512; // small buffers since every object is its own file
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    public static final int MAGIC = 0x6567694E;

    public FileBuilder(int initialSize, ByteBufferFactory byteBufferFactory) {
        if (initialSize <= 4) { // 4 is the size of magic
            initialSize = DEFAULT_BUFFER_SIZE;
        }

        this.byteBufferFactory = byteBufferFactory;
        bb = byteBufferFactory.newByteBuffer(initialSize);

        assert bb.order() == ByteOrder.LITTLE_ENDIAN; // idk what else to do lol. it has to be little endian

        bb.putInt(MAGIC);
    }

    public FileBuilder(int initialSize) {
        this(initialSize, ByteBufferFactory.heapByteBufferFactory());
    }

    public FileBuilder() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public FileBuilder reset() {
        bb.clear();
        nested = false;
        finished = false;

        assert bb.order() == ByteOrder.LITTLE_ENDIAN; // idk what else to do lol. it has to be little endian

        bb.putInt(MAGIC);

        return this;
    }

    static ByteBuffer growByteBuffer(ByteBuffer bb, ByteBufferFactory factory) {
        int oldSize = bb.capacity();
        int newSize;

        if (oldSize == 0) {
            newSize = DEFAULT_BUFFER_SIZE;
        } else {
            if (oldSize == MAX_BUFFER_SIZE) {
                throw new RuntimeException("how the hell did you get a 2gb config file :sob:");
            }

            newSize = (oldSize & 0xC0000000) != 0 ? MAX_BUFFER_SIZE : oldSize << 1;
        }

        ByteBuffer newBB = factory.newByteBuffer(newSize);
        newBB.put(bb);

        return newBB;
    }

    public int getPosition() {
        return bb.position();
    }

    public ByteBuffer getBuffer() {
        finished();
        return bb;
    }

    public byte[] getBufferAsByteArray() {
        finished();

        byte[] array = new byte[bb.position()];
        bb.rewind();
        bb.get(array);

        return array;
    }

    public void prep(int size, int additionalPadding) {
        int align = ((~(bb.position() + additionalPadding)) + 1) & (size - 1);

        while (bb.capacity() < align + size + additionalPadding) {
            bb = growByteBuffer(bb, byteBufferFactory);
        }
    }

    public void putBoolean(boolean b) {
        bb.put((byte) (b ? 1 : 0));
    }

    public void putByte(byte b) {
        bb.put(b);
    }

    public void putShort(short s) {
        bb.putShort(s);
    }

    public void putInt(int i) {
        bb.putInt(i);
    }

    public void putLong(long l) {
        bb.putLong(l);
    }

    public void putFloat(float f) {
        bb.putFloat(f);
    }

    public void putDouble(double d) {
        bb.putDouble(d);
    }

    public void addBoolean(boolean b) {
        prep(Byte.BYTES, 0);
        putBoolean(b);
    }

    public void addByte(byte b) {
        prep(Byte.BYTES, 0);
        putByte(b);
    }

    public void addShort(short s) {
        prep(Short.BYTES, 0);
        putShort(s);
    }

    public void addInt(int i) {
        prep(Integer.BYTES, 0);
        putInt(i);
    }

    public void addLong(long l) {
        prep(Long.BYTES, 0);
        putLong(l);
    }

    public void addFloat(float f) {
        prep(Float.BYTES, 0);
        putFloat(f);
    }

    public void addDouble(double d) {
        prep(Double.BYTES, 0);
        putDouble(d);
    }

    public int addBigInt(BigInteger i) {
        byte[] bytes = i.toByteArray();

        if (bb.order() == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(bytes);
        }

        int pos = startArray(1, bytes.length, 1);
        bb.put(bytes);
        endArray();

        return pos;
    }

    public int startArray(int elementSize, int elementCount, int alignment) {
        notNested();

        int pos = getPosition();

        prep(Integer.BYTES, elementSize * elementCount);
        prep(alignment, elementSize * elementCount);

        bb.putInt(elementCount);

        nested = true;

        return pos;
    }

    public void endArray() {
        if (!nested) {
            throw new RuntimeException("endArray without startArray");
        }

        nested = false;
    }

    public int createString(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        int pos = startArray(1, bytes.length, 1);
        bb.put(bytes);
        endArray();

        return pos;
    }

    public void finished() {
        if (!finished) {
            throw new RuntimeException("buffer can only be accessed after finishing");
        }
    }

    public void notNested() {
        if (nested) {
            throw new RuntimeException("nesting error");
        }
    }

    public void finish() {
        finished = true;
    }

    public void print(FileOutputStream out) throws IOException {
        out.write(getBufferAsByteArray());
    }

    public void print(Path path) throws IOException {
        Files.write(path, getBufferAsByteArray());
    }
}
