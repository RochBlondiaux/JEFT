package me.rochblondiaux.encryption;

import org.junit.jupiter.api.*;

import java.io.File;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityManagerTest {

    private static final File file = new File("C:\\Users\\rochb\\Desktop\\test\\test.txt");
    private static final File encryptedFile = new File(file.getParentFile(), "encrypted-test.txt");
    private static final File decryptedFile = new File(file.getParentFile(), "decrypted-test.txt");
    private static TestSecurityManager manager;

    @BeforeAll
    static void load() {
        manager = new TestSecurityManager();
    }

    @Test
    @Order(1)
    public void testEncryption() throws Exception {
        if (manager == null || !file.exists()) return;
        manager.encrypt(file, encryptedFile);
    }

    @Test
    @Order(2)
    public void testDecryption() throws Exception {
        if (manager == null || !encryptedFile.exists()) return;
        manager.decrypt(encryptedFile, decryptedFile);
    }

}
