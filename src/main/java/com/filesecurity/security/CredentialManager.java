package com.filesecurity.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Secure credential storage with Argon2 password hashing
 */
public class CredentialManager {
    private static final Logger logger = LoggerFactory.getLogger(CredentialManager.class);

    private Map<String, CredentialEntry> credentials;
    private Argon2 argon2;

    private static class CredentialEntry {
        String username;
        String passwordHash;
        String email;
        Date createdAt;
        Date lastModified;
        boolean active;

        CredentialEntry(String username, String passwordHash, String email) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.email = email;
            this.createdAt = new Date();
            this.lastModified = new Date();
            this.active = true;
        }
    }

    public CredentialManager() {
        this.credentials = new HashMap<>();
        this.argon2 = Argon2Factory.create();
    }

    /**
     * Register a new user with password hashing
     */
    public boolean registerUser(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Invalid username provided");
            return false;
        }

        if (credentials.containsKey(username)) {
            logger.warn("User already exists: {}", username);
            return false;
        }

        try {
            String passwordHash = argon2.hash(4, 1024 * 1024, 4, password);
            credentials.put(username, new CredentialEntry(username, passwordHash, email));
            logger.info("User registered successfully: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Failed to register user: {}", username, e);
            return false;
        }
    }

    /**
     * Verify user credentials
     */
    public boolean verifyCredentials(String username, String password) {
        if (username == null || password == null) {
            logger.warn("Null credentials provided for verification");
            return false;
        }

        CredentialEntry entry = credentials.get(username);
        if (entry == null || !entry.active) {
            logger.warn("User not found or inactive: {}", username);
            return false;
        }

        try {
            boolean verified = argon2.verify(entry.passwordHash, password);
            if (!verified) {
                logger.warn("Failed authentication attempt for user: {}", username);
            } else {
                logger.debug("User authenticated successfully: {}", username);
            }
            return verified;
        } catch (Exception e) {
            logger.error("Credential verification error for user: {}", username, e);
            return false;
        }
    }

    /**
     * Update user password
     */
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (!verifyCredentials(username, oldPassword)) {
            logger.warn("Password update failed: invalid old password for user: {}", username);
            return false;
        }

        try {
            String newHash = argon2.hash(4, 1024 * 1024, 4, newPassword);
            CredentialEntry entry = credentials.get(username);
            entry.passwordHash = newHash;
            entry.lastModified = new Date();
            logger.info("Password updated for user: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update password for user: {}", username, e);
            return false;
        }
    }

    /**
     * Deactivate user account
     */
    public boolean deactivateUser(String username) {
        CredentialEntry entry = credentials.get(username);
        if (entry == null) {
            logger.warn("User not found for deactivation: {}", username);
            return false;
        }

        entry.active = false;
        logger.info("User deactivated: {}", username);
        return true;
    }

    /**
     * Check if user exists and is active
     */
    public boolean userExists(String username) {
        CredentialEntry entry = credentials.get(username);
        return entry != null && entry.active;
    }

    /**
     * Get user email
     */
    public String getUserEmail(String username) {
        CredentialEntry entry = credentials.get(username);
        return entry != null ? entry.email : null;
    }

    /**
     * Clear all credentials (for testing)
     */
    public void clear() {
        credentials.clear();
        logger.warn("All credentials cleared");
    }
}
