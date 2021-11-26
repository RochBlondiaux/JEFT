package me.rochblondiaux.encryption;

import me.rochblondiaux.encryption.files.AESEncryptedStream;
import me.rochblondiaux.encryption.keys.AESKeyProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.io.File;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class TestSecurityManager extends SecurityManager {

    public TestSecurityManager() {
        super(new File("C:\\Users\\rochb\\Desktop\\test"), "AES", new BouncyCastlePQCProvider(), 16, new AESKeyProvider(), new AESEncryptedStream());
    }

}
