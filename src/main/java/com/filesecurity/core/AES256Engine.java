package com.filesecurity.core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES-256 encryption and decryption engine for secure file processing.
 * Provides symmetric encryption for confidentiality and data protection.
 */
public class AES256Engine {
    private static final Logger logger = LoggerFactory.getLogger(AES256Engine.class);
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    private SecretKey secretKey;

    /**
     * Initialize with a 256-bit secret key
     */
    public AES256Engine(byte[] keyBytes) {
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Key must be 256 bits (32 bytes)");
        }
        this.secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, ALGORITHM);
    }

    /**
     * Generate a new random 256-bit secret key
     */
    public static byte[] generateKeyBytes() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey().getEncoded();
    }

    /**
     * Encrypt plain text
     */
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        logger.debug("Text encrypted successfully, {} bytes", encryptedBytes.length);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypt encrypted text
     */
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        logger.debug("Text decrypted successfully, {} bytes", decryptedBytes.length);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Encrypt file contents
     */
    public byte[] encryptFile(Path filePath) throws Exception {
        byte[] fileBytes = Files.readAllBytes(filePath);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(fileBytes);
        logger.info("File encrypted: {}, original: {} bytes, encrypted: {} bytes", 
                    filePath, fileBytes.length, encrypted.length);
        return encrypted;
    }

    /**
     * Decrypt file contents
     */
    public byte[] decryptFile(byte[] encryptedBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        logger.info("File decrypted successfully, {} bytes", decrypted.length);
        return decrypted;
    }

    /**
     * Get the secret key in encoded form
     */
    public byte[] getKeyBytes() {
        return secretKey.getEncoded();
    }
}
