package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert suspend fun insert(user: UserEntity)
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?
    @Query("SELECT * FROM users WHERE username = :username AND active = 1 LIMIT 1")
    suspend fun findActiveByUsername(username: String): UserEntity?
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
    @Query("SELECT * FROM users WHERE active = 1 ORDER BY displayName")
    fun observeActive(): Flow<List<UserEntity>>
}
