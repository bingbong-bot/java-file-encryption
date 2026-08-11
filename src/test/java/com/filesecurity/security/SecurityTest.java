package com.filesecurity.security;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Unit tests for RBAC and credential management
 */
public class SecurityTest {
    private RoleBasedAccessControl rbac;
    private CredentialManager credentialManager;

    @Before
    public void setUp() {
        rbac = new RoleBasedAccessControl();
        credentialManager = new CredentialManager();
    }

    // RBAC Tests
    @Test
    public void testAdminHasAllPermissions() {
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, 
                                      RoleBasedAccessControl.Permission.READ));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, 
                                      RoleBasedAccessControl.Permission.WRITE));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, 
                                      RoleBasedAccessControl.Permission.DELETE));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, 
                                      RoleBasedAccessControl.Permission.SHARE));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, 
                                      RoleBasedAccessControl.Permission.AUDIT));
    }

    @Test
    public void testUserHasCorrectPermissions() {
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.USER, 
                                      RoleBasedAccessControl.Permission.READ));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.USER, 
                                      RoleBasedAccessControl.Permission.WRITE));
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.USER, 
                                      RoleBasedAccessControl.Permission.SHARE));
        assertFalse(rbac.hasPermission(RoleBasedAccessControl.Role.USER, 
                                       RoleBasedAccessControl.Permission.DELETE));
        assertFalse(rbac.hasPermission(RoleBasedAccessControl.Role.USER, 
                                       RoleBasedAccessControl.Permission.AUDIT));
    }

    @Test
    public void testViewerHasOnlyReadPermission() {
        assertTrue(rbac.hasPermission(RoleBasedAccessControl.Role.VIEWER, 
                                      RoleBasedAccessControl.Permission.READ));
        assertFalse(rbac.hasPermission(RoleBasedAccessControl.Role.VIEWER, 
                                       RoleBasedAccessControl.Permission.WRITE));
        assertFalse(rbac.hasPermission(RoleBasedAccessControl.Role.VIEWER, 
                                       RoleBasedAccessControl.Permission.DELETE));
    }

    @Test
    public void testNullRoleReturnsNoAccess() {
        assertFalse(rbac.hasPermission(null, RoleBasedAccessControl.Permission.READ));
    }

    @Test
    public void testNullPermissionReturnsNoAccess() {
        assertFalse(rbac.hasPermission(RoleBasedAccessControl.Role.ADMIN, null));
    }

    @Test
    public void testCanPerformFileOperation() {
        assertTrue(rbac.canPerformFileOperation(RoleBasedAccessControl.Role.USER, "read"));
        assertTrue(rbac.canPerformFileOperation(RoleBasedAccessControl.Role.USER, "write"));
        assertFalse(rbac.canPerformFileOperation(RoleBasedAccessControl.Role.VIEWER, "write"));
    }

    // Credential Manager Tests
    @Test
    public void testRegisterNewUser() {
        assertTrue(credentialManager.registerUser("john", "password123", "john@example.com"));
        assertTrue(credentialManager.userExists("john"));
    }

    @Test
    public void testRegisterDuplicateUser() {
        credentialManager.registerUser("jane", "pass", "jane@example.com");
        assertFalse(credentialManager.registerUser("jane", "different", "jane2@example.com"));
    }

    @Test
    public void testVerifyValidCredentials() {
        credentialManager.registerUser("alice", "secret123", "alice@example.com");
        assertTrue(credentialManager.verifyCredentials("alice", "secret123"));
    }

    @Test
    public void testVerifyInvalidPassword() {
        credentialManager.registerUser("bob", "correct", "bob@example.com");
        assertFalse(credentialManager.verifyCredentials("bob", "wrong"));
    }

    @Test
    public void testVerifyNonExistentUser() {
        assertFalse(credentialManager.verifyCredentials("notexist", "password"));
    }

    @Test
    public void testUpdatePassword() {
        credentialManager.registerUser("charlie", "oldpass", "charlie@example.com");
        assertTrue(credentialManager.updatePassword("charlie", "oldpass", "newpass"));
        assertTrue(credentialManager.verifyCredentials("charlie", "newpass"));
        assertFalse(credentialManager.verifyCredentials("charlie", "oldpass"));
    }

    @Test
    public void testUpdatePasswordWithWrongOldPassword() {
        credentialManager.registerUser("dave", "correct", "dave@example.com");
        assertFalse(credentialManager.updatePassword("dave", "wrong", "newpass"));
    }

    @Test
    public void testDeactivateUser() {
        credentialManager.registerUser("eve", "pass", "eve@example.com");
        assertTrue(credentialManager.deactivateUser("eve"));
        assertFalse(credentialManager.userExists("eve"));
        assertFalse(credentialManager.verifyCredentials("eve", "pass"));
    }

    @Test
    public void testGetUserEmail() {
        credentialManager.registerUser("frank", "pass", "frank@example.com");
        assertEquals("frank@example.com", credentialManager.getUserEmail("frank"));
    }

    @Test
    public void testGetNonExistentUserEmail() {
        assertNull(credentialManager.getUserEmail("notexist"));
    }

    @Test
    public void testInvalidUsername() {
        assertFalse(credentialManager.registerUser("", "pass", "test@example.com"));
        assertFalse(credentialManager.registerUser(null, "pass", "test@example.com"));
    }
}
