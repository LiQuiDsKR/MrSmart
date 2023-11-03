package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.mrsmart.standard.membership.Membership
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.tool.Tool
import java.lang.reflect.Member

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "StandardInfo.db"

        private const val TABLE_Membership_NAME = "Membership"
        private const val COLUMN_Membership_ID = "id"
        private const val COLUMN_Membership_CODE = "code"
        private const val COLUMN_Membership_PASSWORD = "password"
        private const val COLUMN_Membership_NAME = "name"
        private const val COLUMN_Membership_PART = "part"
        private const val COLUMN_Membership_SUBPART = "subpart"
        private const val COLUMN_Membership_MAINPART = "mainpart"
        private const val COLUMN_Membership_ROLE = "role"
        private const val COLUMN_Membership_EMPLOYMENT_STATE = "employment_state"

        private const val TABLE_TOOL_NAME = "Tool"
        private const val COLUMN_TOOL_ID = "tool_id"
        private const val COLUMN_TOOL_MAINGROUP = "tool_maingroup"
        private const val COLUMN_TOOL_SUBGROUP = "tool_subgroup"
        private const val COLUMN_TOOL_CODE = "tool_code"
        private const val COLUMN_TOOL_KRNAME = "tool_krname"
        private const val COLUMN_TOOL_ENGNAME = "tool_engname"
        private const val COLUMN_TOOL_SPEC = "tool_spec"
        private const val COLUMN_TOOL_UNIT = "tool_unit"
        private const val COLUMN_TOOL_PRICE = "tool_price"
        private const val COLUMN_TOOL_REPLACEMENTCYCLE = "tool_replacementcycle"
        private const val COLUMN_TOOL_BUYCODE = "tool_buycode"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createMembershipTableQuery = "CREATE TABLE $TABLE_Membership_NAME " +
                "($COLUMN_Membership_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_Membership_CODE TEXT, " +
                "$COLUMN_Membership_PASSWORD TEXT, " +
                "$COLUMN_Membership_NAME TEXT, " +
                "$COLUMN_Membership_PART TEXT, " +
                "$COLUMN_Membership_SUBPART TEXT, " +
                "$COLUMN_Membership_MAINPART TEXT, " +
                "$COLUMN_Membership_ROLE TEXT, " +
                "$COLUMN_Membership_EMPLOYMENT_STATE TEXT)"

        val createToolTableQuery = "CREATE TABLE $TABLE_TOOL_NAME " +
                "($COLUMN_TOOL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TOOL_MAINGROUP TEXT, " +
                "$COLUMN_TOOL_SUBGROUP TEXT, " +
                "$COLUMN_TOOL_CODE TEXT, " +
                "$COLUMN_TOOL_KRNAME TEXT, " +
                "$COLUMN_TOOL_ENGNAME TEXT, " +
                "$COLUMN_TOOL_SPEC TEXT, " +
                "$COLUMN_TOOL_UNIT TEXT, " +
                "$COLUMN_TOOL_PRICE INTEGER, " +
                "$COLUMN_TOOL_REPLACEMENTCYCLE INTEGER, " +
                "$COLUMN_TOOL_BUYCODE TEXT)"

        db.execSQL(createMembershipTableQuery)
        db.execSQL(createToolTableQuery)
    }

    // 데이터베이스 도우미 클래스의 onUpgrade 메서드 내에서 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_Membership_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOL_NAME")

        onCreate(db)
    }


    fun insertMembershipData(
        membershipId: Long,
        membershipCode: String,
        membershipPassword: String,
        membershipName: String,
        membershipPart: String,
        membershipSubpart: String,
        membershipMainpart: String,
        membershipRole: String,
        membershipEmploymentState: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_Membership_ID, membershipId)
        values.put(COLUMN_Membership_CODE, membershipCode)
        values.put(COLUMN_Membership_PASSWORD, membershipPassword)
        values.put(COLUMN_Membership_NAME, membershipName)
        values.put(COLUMN_Membership_PART, membershipPart)
        values.put(COLUMN_Membership_SUBPART, membershipSubpart)
        values.put(COLUMN_Membership_MAINPART, membershipMainpart)
        values.put(COLUMN_Membership_ROLE, membershipRole)
        values.put(COLUMN_Membership_EMPLOYMENT_STATE, membershipEmploymentState)

        val db = this.writableDatabase
        val id = db.insert(TABLE_Membership_NAME, null, values)

        db.close()
        return id
    }

    fun updateMembershipData(
        membershipId: Long,
        membershipCode: String,
        membershipPassword: String,
        membershipName: String,
        membershipPart: String,
        membershipSubpart: String,
        membershipMainpart: String,
        membershipRole: String,
        membershipEmploymentState: String
    ): Int {
        val values = ContentValues()
        values.put(COLUMN_Membership_ID, membershipId)
        values.put(COLUMN_Membership_CODE, membershipCode)
        values.put(COLUMN_Membership_PASSWORD, membershipPassword)
        values.put(COLUMN_Membership_NAME, membershipName)
        values.put(COLUMN_Membership_PART, membershipPart)
        values.put(COLUMN_Membership_SUBPART, membershipSubpart)
        values.put(COLUMN_Membership_MAINPART, membershipMainpart)
        values.put(COLUMN_Membership_ROLE, membershipRole)
        values.put(COLUMN_Membership_EMPLOYMENT_STATE, membershipEmploymentState)

        val db = this.writableDatabase
        val updatedRows = db.update(TABLE_TOOL_NAME, values, "$COLUMN_TOOL_ID = ?", arrayOf(membershipId.toString()))
        db.close()
        return updatedRows
    }

    fun insertToolData(
        toolId: Long,
        toolMaingroup: String,
        toolSubgroup: String,
        toolCode: String,
        toolKrName: String,
        toolEngName: String,
        toolSpec: String,
        toolUnit: String,
        toolPrice: Int,
        toolReplacementCycle: Int,
        //toolBuyCode: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_TOOL_ID, toolId)
        values.put(COLUMN_TOOL_MAINGROUP, toolMaingroup)
        values.put(COLUMN_TOOL_SUBGROUP, toolSubgroup)
        values.put(COLUMN_TOOL_CODE, toolCode)
        values.put(COLUMN_TOOL_KRNAME, toolKrName)
        values.put(COLUMN_TOOL_ENGNAME, toolEngName)
        values.put(COLUMN_TOOL_SPEC, toolSpec)
        values.put(COLUMN_TOOL_UNIT, toolUnit)
        values.put(COLUMN_TOOL_PRICE, toolPrice)
        values.put(COLUMN_TOOL_REPLACEMENTCYCLE, toolReplacementCycle)
        //values.put(COLUMN_TOOL_BUYCODE, toolBuyCode)

        val db = this.writableDatabase
        val id = db.insert(TABLE_TOOL_NAME, null, values)
        db.close()
        return id
    }

    fun updateToolData(
        toolId: Long,
        toolMaingroup: String,
        toolSubgroup: String,
        toolCode: String,
        toolKrName: String,
        toolEngName: String,
        toolSpec: String,
        toolUnit: String,
        toolPrice: Int,
        toolReplacementCycle: Int,
        //toolBuyCode: String
    ): Int {
        val values = ContentValues()
        values.put(COLUMN_TOOL_MAINGROUP, toolMaingroup)
        values.put(COLUMN_TOOL_SUBGROUP, toolSubgroup)
        values.put(COLUMN_TOOL_CODE, toolCode)
        values.put(COLUMN_TOOL_KRNAME, toolKrName)
        values.put(COLUMN_TOOL_ENGNAME, toolEngName)
        values.put(COLUMN_TOOL_SPEC, toolSpec)
        values.put(COLUMN_TOOL_UNIT, toolUnit)
        values.put(COLUMN_TOOL_PRICE, toolPrice)
        values.put(COLUMN_TOOL_REPLACEMENTCYCLE, toolReplacementCycle)
        //values.put(COLUMN_TOOL_BUYCODE, toolBuyCode)

        val db = this.writableDatabase
        val updatedRows = db.update(TABLE_TOOL_NAME, values, "$COLUMN_TOOL_ID = ?", arrayOf(toolId.toString()))
        db.close()
        return updatedRows
    }

    @SuppressLint("Range")
    fun getMembershipByCode(codeToFind: String): MembershipSQLite {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_Membership_ID, $COLUMN_Membership_NAME, $COLUMN_Membership_CODE, $COLUMN_Membership_PASSWORD, $COLUMN_Membership_PART, $COLUMN_Membership_SUBPART, $COLUMN_Membership_MAINPART, $COLUMN_Membership_ROLE, $COLUMN_Membership_EMPLOYMENT_STATE FROM $TABLE_Membership_NAME WHERE $COLUMN_Membership_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)
        lateinit var membershipSQLite: MembershipSQLite

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_Membership_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_NAME))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_CODE))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_PASSWORD))
            val part = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_PART))
            val subPart = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_SUBPART))
            val mainPart = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_MAINPART))
            val role = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_ROLE))
            val employmentStatus = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_EMPLOYMENT_STATE))

            membershipSQLite = MembershipSQLite(id, name, code, password, part, subPart, mainPart, role, employmentStatus)
        }

        cursor.close()
        db.close()
        return membershipSQLite
    }

    @SuppressLint("Range")
    fun getMembershipPasswordById(codeToFind: String): String? {
        val db = this.readableDatabase
        var password: String? = null
        val query = "SELECT $COLUMN_Membership_PASSWORD FROM $TABLE_Membership_NAME WHERE $COLUMN_Membership_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_PASSWORD))
        }

        cursor.close()
        db.close()
        return password
    }
    @SuppressLint("Range")
    fun getMembershipNameById(codeToFind: String): String? {
        val db = this.readableDatabase
        var password: String? = null
        val query = "SELECT $COLUMN_Membership_NAME FROM $TABLE_Membership_NAME WHERE $COLUMN_Membership_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_NAME))
        }

        cursor.close()
        db.close()
        return password
    }

    @SuppressLint("Range")
    fun getToolNameByCode(codeToFind: String): String? {
        val db = this.readableDatabase
        var name: String? = null
        val query = "SELECT $COLUMN_TOOL_KRNAME FROM $TABLE_TOOL_NAME WHERE $COLUMN_TOOL_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_KRNAME))
        }

        cursor.close()
        db.close()
        return name
    }
    @SuppressLint("Range")
    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val query = "SELECT * FROM $TABLE_Membership_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_Membership_ID))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_CODE))
            val password = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_PASSWORD))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_NAME))
            val part = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_PART))
            val subpart = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_SUBPART))
            val mainpart = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_MAINPART))
            val role = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_ROLE))
            val employmentState = cursor.getString(cursor.getColumnIndex(COLUMN_Membership_EMPLOYMENT_STATE))

            val user = User(id, code, password, name, part, subpart, mainpart, role, employmentState)
            userList.add(user)
        }

        cursor.close()
        db.close()

        return userList
    }

}
