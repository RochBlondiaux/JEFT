package me.rochblondiaux.commons.models.slices.distributor;

import lombok.Getter;
import me.rochblondiaux.commons.models.network.PacketTransporter;
import me.rochblondiaux.commons.models.slices.FileSlice;
import me.rochblondiaux.commons.models.slices.transporter.SliceTransporter;
import me.rochblondiaux.commons.utils.UnEditableSet;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
public class EnzoDistributor<T extends PacketTransporter<?>> implements SliceDistributor<T> {

    private final SliceTransporter<T> transporter;

    private final ConcurrentLinkedQueue<FileSlice> queue;
    private final UnEditableSet<FileSlice> slices;

    public EnzoDistributor(SliceTransporter<T> transporter, Set<FileSlice> slices) {
        this.transporter = transporter;
        this.queue = new ConcurrentLinkedQueue<>(slices);
        this.slices = new UnEditableSet<>(slices);
    }

    @Override
    public Optional<FileSlice> getNextSlice() {
        return Optional.ofNullable(queue.poll());
    }
}
