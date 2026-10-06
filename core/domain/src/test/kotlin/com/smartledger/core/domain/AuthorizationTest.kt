package com.smartledger.core.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthorizationTest {
    @Test fun viewerCannotMutate() {
        assertFalse(PermissionPolicy.allowed(Role.VIEWER, Permission.MANAGE_SALES))
        assertTrue(PermissionPolicy.allowed(Role.VIEWER, Permission.VIEW_REPORTS))
    }
    @Test fun ownerCanManageBackup() {
        assertTrue(PermissionPolicy.allowed(Role.OWNER, Permission.MANAGE_BACKUP))
        assertFalse(PermissionPolicy.allowed(Role.ADMIN, Permission.MANAGE_BACKUP))
    }
}
