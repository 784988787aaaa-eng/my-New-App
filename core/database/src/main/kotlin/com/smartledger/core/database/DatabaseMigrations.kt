package com.smartledger.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS products (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, sku TEXT, unitId TEXT NOT NULL, costMinorUnits INTEGER NOT NULL, priceMinorUnits INTEGER NOT NULL, minimumStock INTEGER NOT NULL, archived INTEGER NOT NULL DEFAULT 0)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_products_name ON products(name)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_products_sku ON products(sku)")
            db.execSQL("CREATE TABLE IF NOT EXISTS stock_movements (id TEXT NOT NULL PRIMARY KEY, productId TEXT NOT NULL, quantityBaseUnits INTEGER NOT NULL, kind TEXT NOT NULL, referenceId TEXT NOT NULL, createdAt INTEGER NOT NULL, FOREIGN KEY(productId) REFERENCES products(id) ON DELETE RESTRICT)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_stock_movements_productId ON stock_movements(productId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_stock_movements_createdAt ON stock_movements(createdAt)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_stock_movements_referenceId ON stock_movements(referenceId)")
            db.execSQL("CREATE TABLE IF NOT EXISTS sales (id TEXT NOT NULL PRIMARY KEY, personId TEXT, totalMinorUnits INTEGER NOT NULL, paidMinorUnits INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS sale_lines (saleId TEXT NOT NULL, lineNo INTEGER NOT NULL, productId TEXT NOT NULL, quantity INTEGER NOT NULL, unitPriceMinorUnits INTEGER NOT NULL, PRIMARY KEY(saleId, lineNo))")
        }
    }
}
