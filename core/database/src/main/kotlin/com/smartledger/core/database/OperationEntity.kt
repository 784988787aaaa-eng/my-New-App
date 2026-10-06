package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "operations", foreignKeys = [ForeignKey(entity = PersonEntity::class, parentColumns = ["id"], childColumns = ["personId"], onDelete = ForeignKey.RESTRICT)], indices = [Index("personId"), Index("createdAt")])
data class OperationEntity(@PrimaryKey val id: String, val personId: String, val direction: String, val amountMinorUnits: Long, val note: String?, val createdAt: Long)
