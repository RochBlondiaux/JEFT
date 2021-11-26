package me.rochblondiaux.commons.models.transfers.result;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public enum FileTransferResult {
    UNKNOWN_ERROR,
    MISSING_CHUNK,
    SLICING_ERROR,
    ENCRYPTION_ERROR,
    DECRYPTION_ERROR,
    SUCCESSFUL
}
