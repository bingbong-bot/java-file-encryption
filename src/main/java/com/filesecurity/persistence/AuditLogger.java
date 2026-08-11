package com.filesecurity.persistence;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite-based audit logging system for file operations and security events
 */
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);
    private String dbPath;
    private Connection connection;

    public AuditLogger(String dbPath) throws SQLException {
        this.dbPath = dbPath;
        initializeDatabase();
    }

    /**
     * Initialize SQLite database for audit logs
     */
    private void initializeDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createAuditTable();
            logger.info("Audit database initialized at: {}", dbPath);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to initialize audit database", e);
            throw new SQLException("Database initialization failed", e);
        }
    }

    /**
     * Create audit log table if not exists
     */
    private void createAuditTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS audit_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT NOT NULL,
                username TEXT NOT NULL,
                action TEXT NOT NULL,
                resource TEXT,
                status TEXT NOT NULL,
                details TEXT,
                ip_address TEXT,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            logger.debug("Audit table created or verified");
        }
    }

    /**
     * Log a security event
     */
    public synchronized void logEvent(String username, String action, String resource, 
                                      String status, String details) {
        String insertSQL = """
            INSERT INTO audit_logs (timestamp, username, action, resource, status, details)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, LocalDateTime.now().toString());
            pstmt.setString(2, username);
            pstmt.setString(3, action);
            pstmt.setString(4, resource);
            pstmt.setString(5, status);
            pstmt.setString(6, details);
            pstmt.executeUpdate();
            logger.debug("Audit event logged: user={}, action={}, resource={}, status={}", 
                        username, action, resource, status);
        } catch (SQLException e) {
            logger.error("Failed to log audit event", e);
        }
    }

    /**
     * Log file encryption
     */
    public void logFileEncryption(String username, String filePath, long fileSize, String status) {
        logEvent(username, "ENCRYPT_FILE", filePath, status, 
                "File size: " + fileSize + " bytes");
    }

    /**
     * Log file decryption
     */
    public void logFileDecryption(String username, String filePath, String status) {
        logEvent(username, "DECRYPT_FILE", filePath, status, "File decryption operation");
    }

    /**
     * Log access attempt
     */
    public void logAccessAttempt(String username, String resource, boolean success) {
        String status = success ? "SUCCESS" : "FAILURE";
        logEvent(username, "ACCESS_ATTEMPT", resource, status, 
                "Access " + (success ? "granted" : "denied"));
    }

    /**
     * Retrieve audit logs for a user
     */
    public List<Map<String, Object>> getAuditLogs(String username, int limit) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String querySQL = """
            SELECT id, timestamp, username, action, resource, status, details 
            FROM audit_logs 
            WHERE username = ? 
            ORDER BY id DESC 
            LIMIT ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> log = new LinkedHashMap<>();
                    log.put("id", rs.getInt("id"));
                    log.put("timestamp", rs.getString("timestamp"));
                    log.put("username", rs.getString("username"));
                    log.put("action", rs.getString("action"));
                    log.put("resource", rs.getString("resource"));
                    log.put("status", rs.getString("status"));
                    log.put("details", rs.getString("details"));
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve audit logs for user: {}", username, e);
        }

        return logs;
    }

    /**
     * Get total audit log count
     */
    public int getAuditLogCount() {
        String querySQL = "SELECT COUNT(*) as count FROM audit_logs";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {
            return rs.getInt("count");
        } catch (SQLException e) {
            logger.error("Failed to get audit log count", e);
            return 0;
        }
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Audit database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }
}
