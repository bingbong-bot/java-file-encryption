package com.filesecurity.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Role-Based Access Control (RBAC) for managing user permissions
 */
public class RoleBasedAccessControl {
    private static final Logger logger = LoggerFactory.getLogger(RoleBasedAccessControl.class);

    public enum Role {
        ADMIN("admin"),
        USER("user"),
        VIEWER("viewer");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Permission {
        READ("read"),
        WRITE("write"),
        DELETE("delete"),
        SHARE("share"),
        AUDIT("audit");

        private final String value;

        Permission(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private Map<Role, Set<Permission>> rolePermissions;

    public RoleBasedAccessControl() {
        initializeRoles();
    }

    /**
     * Initialize default role-permission mappings
     */
    private void initializeRoles() {
        rolePermissions = new EnumMap<>(Role.class);

        // Admin: Full access
        Set<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        rolePermissions.put(Role.ADMIN, adminPermissions);

        // User: Read, Write, Share
        Set<Permission> userPermissions = EnumSet.of(
                Permission.READ,
                Permission.WRITE,
                Permission.SHARE
        );
        rolePermissions.put(Role.USER, userPermissions);

        // Viewer: Read only
        Set<Permission> viewerPermissions = EnumSet.of(Permission.READ);
        rolePermissions.put(Role.VIEWER, viewerPermissions);

        logger.info("RBAC roles initialized with default permissions");
    }

    /**
     * Check if a role has a specific permission
     */
    public boolean hasPermission(Role role, Permission permission) {
        if (role == null || permission == null) {
            logger.warn("Null role or permission provided for access check");
            return false;
        }

        Set<Permission> permissions = rolePermissions.getOrDefault(role, new HashSet<>());
        boolean hasAccess = permissions.contains(permission);
        
        logger.debug("Access check: role={}, permission={}, granted={}", 
                     role.value, permission.value, hasAccess);
        
        return hasAccess;
    }

    /**
     * Get all permissions for a role
     */
    public Set<Permission> getPermissions(Role role) {
        return Collections.unmodifiableSet(
                rolePermissions.getOrDefault(role, new HashSet<>())
        );
    }

    /**
     * Check if user can perform a file operation
     */
    public boolean canPerformFileOperation(Role role, String operation) {
        Permission permission;
        try {
            permission = Permission.valueOf(operation.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid operation: {}", operation);
            return false;
        }
        return hasPermission(role, permission);
    }
}
