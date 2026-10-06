package com.smartledger.core.domain

enum class Role { OWNER, ADMIN, ACCOUNTANT, SALES, VIEWER }

enum class Permission {
    VIEW_DASHBOARD, MANAGE_PEOPLE, MANAGE_BOOKS, MANAGE_OPERATIONS,
    MANAGE_SALES, MANAGE_PURCHASES, MANAGE_INVENTORY, MANAGE_EXPENSES,
    VIEW_REPORTS, EXPORT_DATA, MANAGE_USERS, MANAGE_SETTINGS, MANAGE_BACKUP
}

object PermissionPolicy {
    fun require(role: Role, permission: Permission) {\n        check(allowed(role, permission)) { "Permission denied: $permission" }\n    }
    fun allowed(role: Role, permission: Permission): Boolean = when (role) {
        Role.OWNER -> true
        Role.ADMIN -> permission !in setOf(Permission.MANAGE_BACKUP)
        Role.ACCOUNTANT -> permission in setOf(Permission.VIEW_DASHBOARD, Permission.MANAGE_PEOPLE, Permission.MANAGE_BOOKS, Permission.MANAGE_OPERATIONS, Permission.MANAGE_EXPENSES, Permission.VIEW_REPORTS, Permission.EXPORT_DATA)
        Role.SALES -> permission in setOf(Permission.VIEW_DASHBOARD, Permission.MANAGE_PEOPLE, Permission.MANAGE_SALES, Permission.MANAGE_INVENTORY)
        Role.VIEWER -> permission in setOf(Permission.VIEW_DASHBOARD, Permission.VIEW_REPORTS)
    }
}
