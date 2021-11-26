package me.rochblondiaux.commons.utils.binary;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Represents an element which can write to a {@link BinaryWriter}.
 */
public interface Writeable {

    /**
     * Writes into a {@link BinaryWriter}.
     *
     * @param writer the writer to write to
     */
    void write(BinaryWriter writer);

}