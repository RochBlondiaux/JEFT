package me.rochblondiaux.server.network.providers;

import lombok.NonNull;
import me.rochblondiaux.network.models.client.NetworkClient;
import me.rochblondiaux.server.network.client.NettyClient;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for creating {@link NetworkClient} from a {@link NettyClientConnection}.
 */
public interface ClientProvider {

    NettyClient provide(@NonNull UUIDProvider uuidProvider, @NonNull NettyClientConnection connection);
}
