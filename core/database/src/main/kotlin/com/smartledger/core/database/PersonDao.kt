package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people WHERE archived = 0 ORDER BY name COLLATE NOCASE")
    fun observePeople(): Flow<List<PersonEntity>>

    @Query("""
        SELECT personId,
               COALESCE(SUM(CASE WHEN direction = 'RECEIVABLE' THEN amountMinorUnits ELSE 0 END), 0) AS receivable,
               COALESCE(SUM(CASE WHEN direction = 'PAYABLE' THEN amountMinorUnits ELSE 0 END), 0) AS payable
        FROM operations
        GROUP BY personId
    """)
    fun observeBalances(): Flow<List<PersonBalanceRow>>

    @Insert suspend fun insert(person: PersonEntity)

    @Query("UPDATE people SET archived = 1 WHERE id = :id")
    suspend fun archive(id: String)
}

data class PersonBalanceRow(
    val personId: String,
    val receivable: Long,
    val payable: Long
)
