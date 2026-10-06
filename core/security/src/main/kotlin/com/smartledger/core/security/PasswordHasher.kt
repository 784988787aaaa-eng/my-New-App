package com.smartledger.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64

data class PasswordHash(val saltBase64: String, val hashBase64: String, val iterations: Int = 210_000)

class PasswordHasher(private val random: SecureRandom = SecureRandom()) {
    fun hash(password: CharArray): PasswordHash {
        require(password.size >= 8) { "Password must contain at least 8 characters" }
        val salt = ByteArray(16).also(random::nextBytes)
        val hash = derive(password, salt, 210_000)
        password.fill('\u0000')
        return PasswordHash(Base64.getEncoder().withoutPadding().encodeToString(salt), Base64.getEncoder().withoutPadding().encodeToString(hash))
    }
    fun verify(password: CharArray, stored: PasswordHash): Boolean {
        return try {
            val salt = Base64.getDecoder().decode(stored.saltBase64)
            val expected = Base64.getDecoder().decode(stored.hashBase64)
            MessageDigest.isEqual(expected, derive(password, salt, stored.iterations))
        } finally { password.fill('\u0000') }
    }
    private fun derive(password: CharArray, salt: ByteArray, iterations: Int): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(password, salt, iterations, 256)).encoded
}
