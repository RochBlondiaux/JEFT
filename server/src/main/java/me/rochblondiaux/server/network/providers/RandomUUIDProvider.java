package me.rochblondiaux.server.network.providers;

import lombok.NonNull;
import me.rochblondiaux.server.network.client.NettyClientConnection;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Implementation of {@link UUIDProvider}, provide unique id randomly
 */
public class RandomUUIDProvider implements UUIDProvider {

    @Override
    public UUID provide(@NonNull NettyClientConnection connection) {
        return UUID.randomUUID();
    }

}
