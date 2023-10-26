package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper (context: Context) :SQLiteOpenHelper (context, "sqliteDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = "CREATE TABLE IF NOT EXISTS tableName (id INTEGER PRIMARY KEY AUTOINCREMENT, json_data TEXT)"
        db.execSQL(createTableSQL)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 업그레이드 시 필요한 작업을 수행합니다.
    }

    public fun insertDB() {
        val dbHelper = DatabaseHelper(MainActivity().baseContext)
        val db = dbHelper.writableDatabase

        val jsonData = MainActivity().jsonData

        val values = ContentValues()
        values.put("json_data", jsonData)

        val rowId = db.insert("tableName", null, values)

        db.close()
    }
    @SuppressLint("Range")
    public fun getNameFromDatabase(nameToFind: String): String? {
        val dbHelper = DatabaseHelper(MainActivity().baseContext)
        val db = dbHelper.readableDatabase

        val query = "SELECT name FROM tableName WHERE name = ?"
        val selectionArgs = arrayOf(nameToFind)

        val cursor = db.rawQuery(query, selectionArgs)

        var result: String? = null

        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("name"))
        }

        cursor.close()
        db.close()

        return result
    }
}