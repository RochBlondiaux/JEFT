package me.rochblondiaux.encryption;

import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.encryption.files.EncryptedStream;
import me.rochblondiaux.encryption.keys.KeyProvider;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.FileUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Security Manager")
public abstract class SecurityManager {

    private final AlgorithmParameters algorithm;
    private final Provider provider;
    private final int keyLength;

    private final KeyProvider keyProvider;
    private final EncryptedStream encryptedStream;

    private final File keyFile;
    private SecretKeySpec secretKeySpec;

    public SecurityManager(File dataFolder, String algorithm, Provider provider, int keyLength, KeyProvider keyProvider, EncryptedStream encryptedStream) {
        this.keyFile = new File(dataFolder, "client.key");
        this.keyLength = keyLength;
        this.keyProvider = keyProvider;
        this.encryptedStream = encryptedStream;
        this.provider = provider;
        insertProvider();
        this.algorithm = initAlgorithm(algorithm);
        initPrivateKey();
    }

    /**
     * Insert security provider at position 1 (default)
     */
    private void insertProvider() {
        Security.insertProviderAt(provider, 1);
    }

    /**
     * Get {@link AlgorithmParameters} from algorithm name.
     *
     * @param algorithmName algorithm name
     * @return {@link AlgorithmParameters}
     */
    private AlgorithmParameters initAlgorithm(String algorithmName) {
        try {
            return AlgorithmParameters.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot found suitable algorithm parameters", e);
            return null;
        }
    }

    /**
     * Load key from file if it exists or create and save it.
     */
    private void initPrivateKey() {
        if (keyFile.exists()) {
            try {
                this.secretKeySpec = this.keyProvider.load(keyFile, algorithm);
            } catch (IOException e) {
                log.error("Cannot process key file", e);
            } catch (DecoderException e) {
                log.error("Cannot decode key file", e);
            }
            return;
        }
        this.secretKeySpec = this.keyProvider.provide(algorithm, keyLength);
        try {
            this.keyProvider.save(secretKeySpec, keyFile);
        } catch (IOException e) {
            log.error("Cannot save key to file", e);
        }
    }

    public void encrypt(File source, File destination) throws IOException {
        if (!destination.exists())
            if (!destination.createNewFile())
                throw new FileNotFoundException("Cannot create destination file!");
        try (InputStream inStr = FileUtils.openInputStream(source)) {
            try (FileOutputStream fileOutStream = FileUtils.openOutputStream(destination)) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    encryptedStream.encodeStream(secretKeySpec, inStr, bos);
                    bos.writeTo(fileOutStream);
                }
            }
        }
    }

    public void decrypt(File source, File destination) throws IOException {
        if (!destination.exists())
            if (!destination.createNewFile())
                throw new FileNotFoundException("Cannot create destination file!");
        if (!destination.exists())
            throw new FileNotFoundException("Cannot find source file!");
        try (InputStream inStr = FileUtils.openInputStream(source)) {
            try (FileOutputStream fileOutStream = FileUtils.openOutputStream(destination)) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    encryptedStream.decodeStream(secretKeySpec, inStr, bos);
                    bos.writeTo(fileOutStream);
                }
            }
        }
    }
}
