package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "people", indices = [Index("name"), Index("phone")])
data class PersonEntity(@PrimaryKey val id: String, val name: String, val phone: String?, val note: String?, val archived: Boolean = false)
