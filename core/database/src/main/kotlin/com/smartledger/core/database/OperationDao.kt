package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OperationDao {
    @Insert suspend fun insert(operation: OperationEntity)
    @Query("SELECT direction, amountMinorUnits FROM operations WHERE personId = :personId ORDER BY createdAt ASC") suspend fun entries(personId: String): List<DirectionAmount>
}

data class DirectionAmount(val direction: String, val amountMinorUnits: Long)
