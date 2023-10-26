package com.liquidskr.btclient

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonToDBTable(jsonData: String) {

    private val gson = Gson()
    private val membershipList: List<Membership>
    private val db: SQLiteDatabase

    init {
        val listType = object : TypeToken<List<Membership>>() {}.type
        membershipList = gson.fromJson(jsonData, listType)

        // 데이터베이스 헬퍼 객체 생성
        val databaseHelper = DatabaseHelper(LobbyActivity())

        // 데이터베이스 쓰기 모드로 열기
        db = databaseHelper.writableDatabase
    }

    fun insertDataIntoMembershipTable() {
        for (membership in membershipList) {
            val values = ContentValues()
            values.put("id", membership.id)
            values.put("code", membership.code)
            values.put("password", membership.password)
            values.put("name", membership.name)

            // Part, SubPart, MainPart, Role, Status 세팅
            val part = membership.partDto?.name
            val subPart = membership.partDto?.subPartDto?.name
            val mainPart = membership.partDto?.subPartDto?.mainPartDto?.name
            val role = membership.role?.name
            val status = membership.employmentStatus?.name

            values.put("part", part)
            values.put("subPart", subPart)
            values.put("mainPart", mainPart)
            values.put("role", role)
            values.put("status", status)

            // Membership 테이블에 데이터 삽입
            db.insert("Membership", null, values)
        }
        db.close()
    }
}
