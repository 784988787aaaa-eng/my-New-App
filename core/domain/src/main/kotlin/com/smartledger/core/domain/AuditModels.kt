package com.smartledger.core.domain

enum class AuditAction { CREATE, UPDATE, DELETE, RESTORE, BACKUP, RESTORE_BACKUP, LOGIN, LOGOUT, PERMISSION_CHANGE }
data class AuditEvent(val id: String, val action: AuditAction, val entityType: String, val entityId: String?, val actorId: String?, val timestamp: Long, val metadata: String?)
