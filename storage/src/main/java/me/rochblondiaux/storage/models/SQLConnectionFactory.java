package me.rochblondiaux.storage.models;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface SQLConnectionFactory {

    /**
     * Initialize sql connection.
     */
    void initialize() throws SQLException;

    /**
     * Shutdown sql connection.
     */
    void shutdown() throws SQLException;

    /**
     * Get SQL connection.
     *
     * @return {@link Connection}
     */
    Connection getConnection() throws SQLException;

}
