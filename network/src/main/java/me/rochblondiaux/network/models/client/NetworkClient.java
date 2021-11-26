package me.rochblondiaux.network.models.client;

import me.rochblondiaux.network.models.NetworkObject;

import java.util.UUID;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface NetworkClient extends NetworkObject {

    /**
     * Get client identifier.
     *
     * @return {@link UUID} client's identifier.
     */
    UUID getUniqueId();

}
