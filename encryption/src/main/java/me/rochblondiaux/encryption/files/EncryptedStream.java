package me.rochblondiaux.encryption.files;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for encrypting and decrypting {@link InputStream} and {@link OutputStream}
 */
public interface EncryptedStream {

    /**
     * @param secretKeySpec
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    void encodeStream(SecretKeySpec secretKeySpec, InputStream inputStream, OutputStream outputStream) throws IOException;

    /**
     * @param secretKeySpec
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    void decodeStream(SecretKeySpec secretKeySpec, InputStream inputStream, OutputStream outputStream) throws IOException;
}
