package me.rochblondiaux.commons.utils.binary;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents an element which can read from a {@link BinaryReader}.
 */
public interface Readable {

    /**
     * Reads from a {@link BinaryReader}.
     *
     * @param reader the reader to read from
     */
    void read(BinaryReader reader);

}