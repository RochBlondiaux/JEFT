package me.rochblondiaux.commons.utils;

import io.netty.buffer.ByteBuf;
import me.rochblondiaux.commons.utils.binary.BinaryWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class Utils {

    public static String[] removeElements(String[] input, String deleteMe) {
        List<String> result = new LinkedList<>();
        for (String item : input)
            if (!deleteMe.equals(item))
                result.add(item);
        return result.toArray(input);
    }

    public static int getVarIntSize(int input) {
        return (input & 0xFFFFFF80) == 0
                ? 1 : (input & 0xFFFFC000) == 0
                ? 2 : (input & 0xFFE00000) == 0
                ? 3 : (input & 0xF0000000) == 0
                ? 4 : 5;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        // Took from velocity
        if ((value & (0xFFFFFFFF << 7)) == 0)
            buf.writeByte(value);
        else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    public static void write3BytesVarInt(ByteBuf buffer, int startIndex, int value) {
        final int indexCache = buffer.writerIndex();
        buffer.writerIndex(startIndex);
        final int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
        buffer.writeMedium(w);
        buffer.writerIndex(indexCache);
    }

    public static int writeEmpty3BytesVarInt(ByteBuf buffer) {
        final int index = buffer.writerIndex();
        buffer.writeMedium(0);
        return index;
    }

    public static int readVarInt(ByteBuf buf) {
        int i = 0;
        final int maxRead = Math.min(5, buf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            final int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128)
                return i;
        }
        throw new RuntimeException("VarInt is too big");
    }

    public static long readVarLong(ByteBuf buffer) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = buffer.readByte();
            long value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10)
                throw new RuntimeException("VarLong is too big");
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeVarLong(BinaryWriter writer, long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0)
                temp |= 0b10000000;
            writer.writeByte(temp);
        } while (value != 0);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        int[] array = new int[4];

        final long uuidMost = uuid.getMostSignificantBits();
        final long uuidLeast = uuid.getLeastSignificantBits();

        array[0] = (int) (uuidMost >> 32);
        array[1] = (int) uuidMost;

        array[2] = (int) (uuidLeast >> 32);
        array[3] = (int) uuidLeast;

        return array;
    }

    public static UUID intArrayToUuid(int[] array) {
        final long uuidMost = (long) array[0] << 32 | array[1] & 0xFFFFFFFFL;
        final long uuidLeast = (long) array[2] << 32 | array[3] & 0xFFFFFFFFL;

        return new UUID(uuidMost, uuidLeast);
    }
}
