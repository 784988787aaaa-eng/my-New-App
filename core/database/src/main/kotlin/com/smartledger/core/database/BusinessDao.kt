package com.smartledger.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao interface BusinessDao {
 @Insert suspend fun insertExpense(value:ExpenseEntity)
 @Query("SELECT * FROM expenses ORDER BY createdAt DESC") fun observeExpenses():Flow<List<ExpenseEntity>>
 @Insert suspend fun insertEmployee(value:EmployeeEntity)
 @Query("SELECT * FROM employees WHERE active=1 ORDER BY name COLLATE NOCASE") fun observeEmployees():Flow<List<EmployeeEntity>>
 @Insert suspend fun insertAudit(value:AuditLogEntity)
 @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 500") fun observeAudit():Flow<List<AuditLogEntity>>
 @Insert suspend fun recycle(value:RecycleBinEntity)
 @Query("SELECT * FROM recycle_bin ORDER BY deletedAt DESC") fun observeRecycle():Flow<List<RecycleBinEntity>>
 @Query("DELETE FROM recycle_bin WHERE id=:id") suspend fun purgeRecycle(id:String)
}
