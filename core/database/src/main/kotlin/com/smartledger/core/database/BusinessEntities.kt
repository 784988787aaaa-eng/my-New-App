package com.smartledger.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="expenses", indices=[Index("createdAt"), Index("category")])
data class ExpenseEntity(@PrimaryKey val id:String,val category:String,val amountMinorUnits:Long,val note:String?,val createdAt:Long)

@Entity(tableName="employees", indices=[Index("name")])
data class EmployeeEntity(@PrimaryKey val id:String,val name:String,val phone:String?,val active:Boolean=true)

@Entity(tableName="audit_logs", indices=[Index("timestamp"),Index("entityType"),Index("entityId")])
data class AuditLogEntity(@PrimaryKey val id:String,val action:String,val entityType:String,val entityId:String?,val actorId:String?,val timestamp:Long,val metadata:String?)

@Entity(tableName="recycle_bin", indices=[Index("deletedAt")])
data class RecycleBinEntity(@PrimaryKey val id:String,val entityType:String,val entityId:String,val payload:String,val deletedAt:Long)
