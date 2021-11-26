package me.rochblondiaux.server.network.providers;

import lombok.NonNull;
import me.rochblondiaux.server.network.client.NettyClient;
import me.rochblondiaux.server.network.client.NettyClientConnection;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Implementations of {@link ClientProvider}.
 */
public class NettyClientProvider implements ClientProvider {

    @Override
    public NettyClient provide(@NonNull UUIDProvider uuidProvider, @NonNull NettyClientConnection connection) {
        return new NettyClient(uuidProvider.provide(connection), connection);
    }

}
