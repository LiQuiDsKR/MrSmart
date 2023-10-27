package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase.db"
        private const val TABLE_NAME = "MyTable"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CODE = "code"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PART = "part"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_EMPLOYMENT_STATE = "employment_state"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CODE TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_PART TEXT, " +
                "$COLUMN_ROLE TEXT, " +
                "$COLUMN_EMPLOYMENT_STATE TEXT)"

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(
        code: String,
        password: String,
        name: String,
        part: String,
        role: String,
        employmentState: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_CODE, code)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_PART, part)
        values.put(COLUMN_ROLE, role)
        values.put(COLUMN_EMPLOYMENT_STATE, employmentState)

        val db = this.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getNameByCode(codeToFind: String): String? {
        val db = this.readableDatabase
        var name: String? = null
        val query = "SELECT $COLUMN_NAME FROM $TABLE_NAME WHERE $COLUMN_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        }

        cursor.close()
        db.close()

        return name
    }
}
