package com.smartledger.core.database
import androidx.room.Dao
import androidx.room.Insert
@Dao interface PurchaseDao { @Insert suspend fun insertPurchase(value:PurchaseEntity); @Insert suspend fun insertLines(values:List<PurchaseLineEntity>) }
