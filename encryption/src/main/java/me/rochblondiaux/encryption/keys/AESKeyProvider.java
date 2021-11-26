package me.rochblondiaux.encryption.keys;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.DrbgParameters;
import java.security.SecureRandom;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * PBE implementation of {@link KeyProvider}
 */
@Slf4j(topic = "AES Key Provider")
public class AESKeyProvider implements KeyProvider {

    @Override
    public SecretKeySpec provide(AlgorithmParameters algorithm, int keyLength) {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[keyLength];
        random.nextBytes(keyBytes, DrbgParameters.nextBytes(keyLength, false, null));

        return new SecretKeySpec(keyBytes, algorithm.getAlgorithm());
    }

    @Override
    public void save(SecretKeySpec secretKeySpec, File file) throws IOException {
        byte[] key = secretKeySpec.getEncoded();
        FileUtils.writeByteArrayToFile(file, Base64.encodeBase64(key));
    }

    @Override
    public SecretKeySpec load(File file, AlgorithmParameters algorithm) throws IOException, DecoderException {
        byte[] data = FileUtils.readFileToByteArray(file);
        byte[] encoded = Base64.decodeBase64(data);
        return new SecretKeySpec(encoded, algorithm.getAlgorithm());
    }

}
