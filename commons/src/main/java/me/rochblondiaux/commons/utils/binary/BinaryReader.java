package me.rochblondiaux.commons.utils.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.rochblondiaux.commons.utils.Utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Class used to read from a byte array.
 * <p>
 * WARNING: not thread-safe.
 */
public class BinaryReader extends InputStream {

    @Getter
    private final ByteBuf buffer;

    public BinaryReader(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public BinaryReader(byte[] bytes) {
        this(Unpooled.wrappedBuffer(bytes));
    }

    public int readVarInt() {
        return Utils.readVarInt(buffer);
    }

    public long readVarLong() {
        return Utils.readVarLong(buffer);
    }

    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public short readShort() {
        return buffer.readShort();
    }

    public char readChar() {
        return buffer.readChar();
    }

    public int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    /**
     * Same as readInt
     */
    public int readInteger() {
        return buffer.readInt();
    }

    /**
     * Same as readInteger, created for parity with BinaryWriter
     */
    public int readInt() {
        return buffer.readInt();
    }

    public long readLong() {
        return buffer.readLong();
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    /**
     * Reads a string size by a var-int.
     * <p>
     * If the string length is higher than {@code maxLength},
     * the code throws an exception and the string bytes are not read.
     *
     * @param maxLength the max length of the string
     * @return the string
     * @throws IllegalStateException if the string length is invalid or higher than {@code maxLength}
     */
    public String readSizedString(int maxLength) {
        final int length = readVarInt();
        if (!buffer.isReadable(length))
            throw new IllegalStateException(String.format("Trying to read a string that is too long (wanted %s, only have %s)", length, buffer.readableBytes()));
        final String str = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
        buffer.skipBytes(length);
        if (str.length() > maxLength)
            throw new IllegalStateException(String.format("String length (%s) was higher than the max length of %s", length, maxLength));
        return str;
    }

    public String readSizedString() {
        return readSizedString(Integer.MAX_VALUE);
    }

    public byte[] readBytes(int length) {
        ByteBuf buf = buffer.readBytes(length);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        return bytes;
    }

    public String[] readSizedStringArray(int maxLength) {
        final int size = readVarInt();
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = readSizedString(maxLength);
        }
        return strings;
    }

    public String[] readSizedStringArray() {
        return readSizedStringArray(Integer.MAX_VALUE);
    }

    public int[] readVarIntArray() {
        final int size = readVarInt();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = readVarInt();
        }
        return array;
    }

    public long[] readLongArray() {
        final int size = readVarInt();
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = readLong();
        }
        return array;
    }

    public byte[] readRemainingBytes() {
        return readBytes(available());
    }

    public UUID readUuid() {
        final long most = readLong();
        final long least = readLong();
        return new UUID(most, least);
    }

    public Date readDate() {
        return new Date(readVarLong());
    }

    /**
     * Creates a new object from the given supplier and calls its {@link Readable#read(BinaryReader)} method with this reader.
     *
     * @param supplier supplier to create new instances of your object
     * @param <T>      the readable object type
     * @return the read object
     */
    public <T extends Readable> T read(Supplier<T> supplier) {
        T result = supplier.get();
        result.read(this);
        return result;
    }

    /**
     * Reads the length of the array to read as a varint, creates the array to contain the readable objects and call
     * their respective {@link Readable#read(BinaryReader)} methods.
     *
     * @param supplier supplier to create new instances of your object
     * @param <T>      the readable object type
     * @return the read objects
     */
    public <T extends Readable> T[] readArray(Supplier<T> supplier) {
        Readable[] result = new Readable[readVarInt()];
        for (int i = 0; i < result.length; i++) {
            result[i] = supplier.get();
            result[i].read(this);
        }
        return (T[]) result;
    }

    @Override
    public int read() {
        return readByte() & 0xFF;
    }

    @Override
    public int available() {
        return buffer.readableBytes();
    }

    /**
     * Records the current position, runs the given Runnable, and then returns the bytes between the position before
     * running the runnable and the position after.
     * Can be used to extract a subsection of this reader's buffer with complex data
     *
     * @param extractor the extraction code, simply call the reader's read* methods here.
     */
    public byte[] extractBytes(Runnable extractor) {
        int startingPosition = getBuffer().readerIndex();
        extractor.run();
        int endingPosition = getBuffer().readerIndex();
        byte[] output = new byte[endingPosition - startingPosition];
        getBuffer().getBytes(startingPosition, output);
        return output;
    }
}