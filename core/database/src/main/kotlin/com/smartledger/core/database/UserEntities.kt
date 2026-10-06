package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index("username", unique = true)])
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val role: String,
    val passwordSalt: String,
    val passwordHash: String,
    val passwordIterations: Int,
    val active: Boolean = true
)
