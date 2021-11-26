package me.rochblondiaux.network.models;

import me.rochblondiaux.network.packets.server.ServerPacket;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface NetworkObject {

    /**
     * Get object connection.
     *
     * @return {@link NetworkObjectConnection} object's connection.
     */
    NetworkObjectConnection<ServerPacket> getConnection();

    /**
     * Initialize {@link NetworkObjectConnection}.
     */
    void initializeConnection();

    /**
     * Disconnect {@link NetworkObjectConnection} from network.
     */
    void disconnect();

    /**
     * Gets the last sent keep alive id.
     *
     * @return the last keep alive id sent to the client
     */
    long getLastKeepAlive();

    /**
     * Used to change internally the last sent last keep alive id.
     * <p>
     * Warning: could lead to have the client kicked because of a wrong keep alive packet.
     *
     * @param lastKeepAlive the new lastKeepAlive id
     */
    void setLastKeepAlive(long lastKeepAlive);

    /**
     * @param answerKeepAlive
     */
    void setAnswerKeepAlive(boolean answerKeepAlive);

    /**
     * @return
     */
    boolean didAnswerKeepAlive();
}
