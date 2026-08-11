package com.filesecurity.core;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for AES256Engine encryption/decryption
 */
public class AES256EngineTest {
    private AES256Engine engine;
    private byte[] testKey;

    @Before
    public void setUp() throws Exception {
        testKey = AES256Engine.generateKeyBytes();
        engine = new AES256Engine(testKey);
    }

    @Test
    public void testEncryptDecryptText() throws Exception {
        String originalText = "This is a secret message!";
        
        String encrypted = engine.encrypt(originalText);
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        
        String decrypted = engine.decrypt(encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    public void testMultipleEncryptions() throws Exception {
        String text = "Test message";
        String encrypted1 = engine.encrypt(text);
        String encrypted2 = engine.encrypt(text);
        
        // Same text encrypted multiple times should produce different results
        // (due to cipher state)
        assertNotNull(encrypted1);
        assertNotNull(encrypted2);
        
        assertEquals(text, engine.decrypt(encrypted1));
        assertEquals(text, engine.decrypt(encrypted2));
    }

    @Test
    public void testEmptyStringEncryption() throws Exception {
        String empty = "";
        String encrypted = engine.encrypt(empty);
        String decrypted = engine.decrypt(encrypted);
        assertEquals(empty, decrypted);
    }

    @Test
    public void testLongStringEncryption() throws Exception {
        String longText = "A".repeat(10000);
        String encrypted = engine.encrypt(longText);
        String decrypted = engine.decrypt(encrypted);
        assertEquals(longText, decrypted);
    }

    @Test
    public void testKeyGeneration() throws Exception {
        byte[] key1 = AES256Engine.generateKeyBytes();
        byte[] key2 = AES256Engine.generateKeyBytes();
        
        assertEquals(32, key1.length); // 256 bits = 32 bytes
        assertEquals(32, key2.length);
        assertNotArrayEquals(key1, key2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKeyLength() {
        byte[] invalidKey = new byte[16]; // Only 128 bits
        new AES256Engine(invalidKey);
    }

    @Test
    public void testSpecialCharacters() throws Exception {
        String specialText = "!@#$%^&*()_+-=[]{}|;:,.<>?/~`";
        String encrypted = engine.encrypt(specialText);
        String decrypted = engine.decrypt(encrypted);
        assertEquals(specialText, decrypted);
    }

    @Test
    public void testUnicodeCharacters() throws Exception {
        String unicodeText = "你好世界🔐 Привет мир";
        String encrypted = engine.encrypt(unicodeText);
        String decrypted = engine.decrypt(encrypted);
        assertEquals(unicodeText, decrypted);
    }
}
