package me.rochblondiaux.server.network.providers;

import lombok.NonNull;
import me.rochblondiaux.server.network.client.NettyClientConnection;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Reponsible for unique id generation.
 */
public interface UUIDProvider {

    /**
     * Generate an {@link UUID} from {@link NettyClientConnection}
     *
     * @param connection client connection
     * @return {@link UUID}
     */
    UUID provide(@NonNull NettyClientConnection connection);
}
