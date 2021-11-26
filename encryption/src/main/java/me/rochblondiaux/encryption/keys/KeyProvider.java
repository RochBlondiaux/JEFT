package me.rochblondiaux.encryption.keys;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.AlgorithmParameters;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Responsible for providing, loading and saving key to
 * {@link me.rochblondiaux.encryption.SecurityManager}
 */
public interface KeyProvider {

    /**
     * Provide {@link KeyParameter} from algorithm and key length.
     *
     * @param algorithm algorithm provided by {@link me.rochblondiaux.encryption.SecurityManager}
     * @param keyLength key length provided by {@link me.rochblondiaux.encryption.SecurityManager}
     * @return {@link KeyParameter} wrapped key
     */
    SecretKeySpec provide(AlgorithmParameters algorithm, int keyLength);

    /**
     * Save {@link SecretKeySpec} to {@link File}
     *
     * @param secretKeySpec {@link SecretKeySpec} secret key.
     * @param file          target file.
     */
    void save(SecretKeySpec secretKeySpec, File file) throws IOException;

    /**
     * Load {@link SecretKeySpec} from {@link File}
     *
     * @param file      {@link File} containing key.
     * @param algorithm {@link AlgorithmParameters} algorithm.
     * @return {@link SecretKey} parsed key.
     */
    SecretKeySpec load(File file, AlgorithmParameters algorithm) throws IOException, DecoderException;
}
