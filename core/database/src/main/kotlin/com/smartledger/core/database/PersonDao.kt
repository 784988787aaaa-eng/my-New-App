package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people WHERE archived = 0 ORDER BY name COLLATE NOCASE") fun observePeople(): Flow<List<PersonEntity>>
    @Insert suspend fun insert(person: PersonEntity)
    @Query("UPDATE people SET archived = 1 WHERE id = :id") suspend fun archive(id: String)
}
