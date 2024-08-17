package cum.jesus.jesusclient.file.reader;

import cum.jesus.jesusclient.file.builder.FileBuilder;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class FileReader {
    private ByteBuffer bb;

    public FileReader(File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            byte[] data = new byte[(int) raf.length()];
            raf.readFully(data);
            raf.close();

            bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            int magic = bb.getInt();
            if (magic != FileBuilder.MAGIC) {
                throw new RuntimeException("incorrect file magic for jesus file format");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FileReader() {

    }

    public FileReader reset(File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            byte[] data = new byte[(int) raf.length()];
            raf.readFully(data);
            raf.close();

            bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            int magic = bb.getInt();
            if (magic != FileBuilder.MAGIC) {
                throw new RuntimeException("incorrect file magic for jesus file format");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public FileReader reset() {
        bb.clear();
        return this;
    }

    public int getPosition() {
        return bb.position();
    }

    public void setPosition(int position) {
        bb.position(position);
    }

    public boolean getBoolean() {
        return bb.get() != 0;
    }

    public boolean getBoolean(int pos) {
        return bb.get(pos) != 0;
    }

    public byte getByte() {
        return bb.get();
    }

    public byte getByte(int pos) {
        return bb.get(pos);
    }

    public short getShort() {
        return bb.getShort();
    }

    public short getShort(int pos) {
        return bb.getShort(pos);
    }

    public int getInt() {
        return bb.getInt();
    }

    public int getInt(int pos) {
        return bb.getInt(pos);
    }

    public long getLong() {
        return bb.getLong();
    }

    public long getLong(int pos) {
        return bb.getLong(pos);
    }

    public float getFloat() {
        return bb.getFloat();
    }

    public float getFloat(int pos) {
        return bb.getFloat(pos);
    }

    public double getDouble() {
        return bb.getDouble();
    }

    public double getDouble(int pos) {
        return bb.getDouble(pos);
    }

    public BigInteger getBigInt() {
        byte[] bytes = getByteArray();

        if (bb.order() == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(bytes);
        }

        return new BigInteger(bytes);
    }

    public BigInteger getBigInt(int pos) {
        byte[] bytes = getByteArray(pos);

        if (bb.order() == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(bytes);
        }

        return new BigInteger(bytes);
    }

    public byte[] getByteArray() {
        int elementCount = bb.getInt();
        byte[] bytes = new byte[elementCount];

        bb.get(bytes);

        return bytes;
    }

    public byte[] getByteArray(int pos) {
        int mark = bb.position(); // because java doesn't know how to write a standard library and it resets for no reason
        bb.position(pos);

        int elementCount = bb.getInt();
        byte[] bytes = new byte[elementCount];

        bb.get(bytes);

        bb.position(mark);

        return bytes;
    }

    public String getString() {
        byte[] bytes = getByteArray();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String getString(int pos) {
        byte[] bytes = getByteArray(pos);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
