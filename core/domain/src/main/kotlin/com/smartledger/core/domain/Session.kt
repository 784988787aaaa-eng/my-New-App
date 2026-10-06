package com.smartledger.core.domain

data class UserSession(
    val userId: String,
    val username: String,
    val displayName: String,
    val role: Role
) {
    fun can(permission: Permission): Boolean = PermissionPolicy.allowed(role, permission)
    fun require(permission: Permission) = PermissionPolicy.require(role, permission)
}
