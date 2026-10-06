package com.smartledger.app.auth

import android.app.Application
import com.smartledger.app.data.AppDatabaseProvider
import com.smartledger.core.database.UserEntity
import com.smartledger.core.domain.Role
import com.smartledger.core.domain.UserSession
import com.smartledger.core.security.PasswordHash
import com.smartledger.core.security.PasswordHasher
import com.smartledger.core.security.SecureStore
import java.util.UUID

class SessionManager(private val application: Application) {
    private val db = AppDatabaseProvider.get(application)
    private val secure = SecureStore(application)
    private val hasher = PasswordHasher()

    fun restore(): UserSession? {
        val id = secure.get("session.userId") ?: return null
        val username = secure.get("session.username") ?: return null
        val name = secure.get("session.displayName") ?: return null
        val role = runCatching { Role.valueOf(secure.get("session.role") ?: return null) }.getOrNull() ?: return null
        return UserSession(id, username, name, role)
    }

    suspend fun login(username: String, password: CharArray): UserSession {
        val user = db.userDao().findActiveByUsername(username.trim()) ?: error("اسم المستخدم أو كلمة المرور غير صحيحة")
        val valid = hasher.verify(password, PasswordHash(user.passwordSalt, user.passwordHash, user.passwordIterations))
        check(valid) { "اسم المستخدم أو كلمة المرور غير صحيحة" }
        return persist(UserSession(user.id, user.username, user.displayName, Role.valueOf(user.role)))
    }

    suspend fun bootstrapOwner(username: String, displayName: String, password: CharArray): UserSession {
        check(db.userDao().count() == 0) { "يوجد مستخدمون بالفعل" }
        val hash = hasher.hash(password)
        val id = UUID.randomUUID().toString()
        db.userDao().insert(UserEntity(id, username.trim(), displayName.trim(), Role.OWNER.name, hash.saltBase64, hash.hashBase64, hash.iterations, true))
        return persist(UserSession(id, username.trim(), displayName.trim(), Role.OWNER))
    }

    fun logout() {
        listOf("session.userId","session.username","session.displayName","session.role").forEach { secure.remove(it) }
    }

    private fun persist(session: UserSession): UserSession {
        secure.put("session.userId", session.userId)
        secure.put("session.username", session.username)
        secure.put("session.displayName", session.displayName)
        secure.put("session.role", session.role.name)
        return session
    }
}
