package me.rochblondiaux.commons.models.slices.transporter;

import lombok.NonNull;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Reponsible for {@link FileSlice} transport.
 */
@FunctionalInterface
public interface SliceTransporter<T extends PacketTransporter<?>> {

    /**
     * Transport {@link FileSlice} from a point A to a point B through network.
     *
     * @param transporter {@link PacketTransporter} packet transporter
     * @param slice       {@link FileSlice} to transport.
     */
    void transport(@NonNull T transporter, @NonNull FileSlice slice);
}
