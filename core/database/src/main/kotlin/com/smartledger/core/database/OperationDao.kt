package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationDao {
    @Insert suspend fun insert(operation: OperationEntity)
    @Query("SELECT direction, amountMinorUnits FROM operations WHERE personId = :personId ORDER BY createdAt ASC")
    suspend fun entries(personId: String): List<DirectionAmount>
    @Query("SELECT COALESCE(SUM(amountMinorUnits),0) FROM operations WHERE personId = :personId AND direction = :direction")
    suspend fun balance(personId: String, direction: String): Long
    @Query("SELECT COALESCE(SUM(CASE WHEN direction = 'RECEIVABLE' THEN amountMinorUnits ELSE 0 END),0) FROM operations")
    fun totalReceivable(): Flow<Long>
    @Query("SELECT COALESCE(SUM(CASE WHEN direction = 'PAYABLE' THEN amountMinorUnits ELSE 0 END),0) FROM operations")
    fun totalPayable(): Flow<Long>
}

data class DirectionAmount(val direction: String, val amountMinorUnits: Long)
