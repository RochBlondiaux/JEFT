package me.rochblondiaux.storage;

import me.rochblondiaux.storage.models.SQLConnectionFactory;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class StorageManager extends SQLUtils {
    public StorageManager(SQLConnectionFactory connectionFactory) {
        super(connectionFactory);
    }
}
