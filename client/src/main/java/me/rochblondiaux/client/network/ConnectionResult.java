package me.rochblondiaux.client.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Getter
@RequiredArgsConstructor
public enum ConnectionResult {
    SUCCESSFUL(null),
    ALREADY_CONNECTED("already.connected"),
    UNKNOWN_HOSTNAME("unknown.hostname"),
    CONNECTION_REFUSED("connection.refused"),
    UNKNOWN_ERROR("unknown.error");

    private final String localizable;
}
