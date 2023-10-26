package com.liquidskr.btclient

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "YourDatabaseName"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Membership (id INTEGER PRIMARY KEY, name TEXT)")
        // 나머지 테이블도 생성합니다.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 스키마가 업데이트될 때 호출됩니다.
        // 여기에서 테이블 업그레이드 논리를 구현합니다.
        // 예를 들어, 기존 테이블을 삭제하고 새로운 테이블을 생성할 수 있습니다.
    }
}
