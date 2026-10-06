package com.smartledger.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test fun hashVerifiesAndWrongPasswordFails() {
        val hasher = PasswordHasher()
        val stored = hasher.hash("CorrectHorseBatteryStaple!".toCharArray())
        assertTrue(hasher.verify("CorrectHorseBatteryStaple!".toCharArray(), stored))
        assertFalse(hasher.verify("wrong-password".toCharArray(), stored))
    }
}
