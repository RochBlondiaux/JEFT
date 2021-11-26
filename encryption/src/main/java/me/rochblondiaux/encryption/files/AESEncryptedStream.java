package me.rochblondiaux.encryption.files;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class AESEncryptedStream implements EncryptedStream {

    static final int AES_NIV_BITS = 128;

    @Override
    public void encodeStream(SecretKeySpec secretKeySpec, InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] ivData = new byte[AES_NIV_BITS / 8];
        new SecureRandom().nextBytes(ivData);

        KeyParameter keyParam = new KeyParameter(secretKeySpec.getEncoded());
        CipherParameters params = new ParametersWithIV(keyParam, ivData);

        BlockCipherPadding padding = new PKCS7Padding();
        BufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
        blockCipher.reset();
        blockCipher.init(true, params);

        outputStream.write(ivData);
        CipherOutputStream cipherOut = new CipherOutputStream(outputStream, blockCipher);
        IOUtils.copy(inputStream, cipherOut);
        cipherOut.close();
    }

    @Override
    public void decodeStream(SecretKeySpec secretKeySpec, InputStream inputStream, OutputStream outputStream) throws IOException {
        int nIvBytes = AES_NIV_BITS / 8;
        byte[] ivBytes = new byte[nIvBytes];
        inputStream.read(ivBytes, 0, nIvBytes);

        KeyParameter keyParam = new KeyParameter(secretKeySpec.getEncoded());
        CipherParameters params = new ParametersWithIV(keyParam, ivBytes);

        BlockCipherPadding padding = new PKCS7Padding();
        BufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
        blockCipher.reset();
        blockCipher.init(false, params);

        CipherInputStream cipherIn = new CipherInputStream(inputStream, blockCipher);
        IOUtils.copy(cipherIn, outputStream);
        cipherIn.close();
    }

}
