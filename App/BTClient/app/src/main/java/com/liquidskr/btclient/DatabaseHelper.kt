package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.RentalRequestSheetApprove
import com.mrsmart.standard.returns.ReturnSheetFormDto
import com.mrsmart.standard.tool.ToolDtoSQLite

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

        private const val TABLE_STANDBY_NAME = "Standby"
        private const val COLUMN_STANDBY_ID = "standby_id"
        private const val COLUMN_STANDBY_JSON = "standby_json"
        private const val COLUMN_STANDBY_TYPE = "standby_type"
        private const val COLUMN_STANDBY_STATUS = "standby_status"
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

        val createStandbyTableQuery = "CREATE TABLE $TABLE_STANDBY_NAME " +
                "($COLUMN_STANDBY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_STANDBY_JSON TEXT, " +
                "$COLUMN_STANDBY_TYPE TEXT, " +
                "$COLUMN_STANDBY_STATUS TEXT)"

        db.execSQL(createMembershipTableQuery)
        db.execSQL(createToolTableQuery)
        db.execSQL(createStandbyTableQuery)
    }

    // 데이터베이스 도우미 클래스의 onUpgrade 메서드 내에서 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_Membership_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOL_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STANDBY_NAME")

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
        toolBuyCode: String
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
        values.put(COLUMN_TOOL_BUYCODE, toolBuyCode)

        val db = this.writableDatabase
        val id = db.insert(TABLE_TOOL_NAME, null, values)
        db.close()
        return id
    }

    fun insertStandbyData(
        //standbyId: Long,
        standbyJson: String,
        standbyType: String,
        standbyStatus: String
    ): Long {
        val values = ContentValues()
        //values.put(COLUMN_STANDBY_ID, standbyId)
        values.put(COLUMN_STANDBY_JSON, standbyJson)
        values.put(COLUMN_STANDBY_TYPE, standbyType)
        values.put(COLUMN_STANDBY_STATUS, standbyStatus)

        val db = this.writableDatabase
        val id = db.insert(TABLE_STANDBY_NAME, null, values)
        db.close()
        return id
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
    fun getAllMemberships(): List<MembershipSQLite> {
        val membershipList = mutableListOf<MembershipSQLite>()
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

            val membership = MembershipSQLite(id, name, code, password, part, subpart, mainpart, role, employmentState)
            membershipList.add(membership)
        }

        cursor.close()
        db.close()

        return membershipList
    }
    @SuppressLint("Range")
    fun getToolByCode(codeToFind: String): ToolDtoSQLite {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TOOL_ID, $COLUMN_TOOL_SUBGROUP, $COLUMN_TOOL_MAINGROUP, $COLUMN_TOOL_CODE, $COLUMN_TOOL_KRNAME, $COLUMN_TOOL_ENGNAME, $COLUMN_TOOL_SPEC, $COLUMN_TOOL_UNIT, $COLUMN_TOOL_PRICE, $COLUMN_TOOL_REPLACEMENTCYCLE, $COLUMN_TOOL_BUYCODE FROM $TABLE_TOOL_NAME WHERE $COLUMN_TOOL_CODE = ?"
        val selectionArgs = arrayOf(codeToFind)
        lateinit var toolDtoSQLite: ToolDtoSQLite

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_TOOL_ID))
            val subGroup = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_SUBGROUP))
            val mainGroup = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_MAINGROUP))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_CODE))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_KRNAME))
            val engName = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_ENGNAME))
            val spec = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_SPEC))
            val unit = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_UNIT))
            val price = cursor.getInt(cursor.getColumnIndex(COLUMN_TOOL_PRICE))
            val replacementCycle = cursor.getInt(cursor.getColumnIndex(COLUMN_TOOL_REPLACEMENTCYCLE))
            val buyCode = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_BUYCODE))

            toolDtoSQLite = ToolDtoSQLite(id, subGroup, mainGroup, code, name, engName, spec, unit, price, replacementCycle, buyCode)
        }

        cursor.close()
        db.close()
        return toolDtoSQLite
    }

    @SuppressLint("Range")
    fun getAllTools(): List<ToolDtoSQLite> {
        val toolList = mutableListOf<ToolDtoSQLite>()
        val query = "SELECT * FROM $TABLE_TOOL_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_TOOL_ID))
            val subGroup = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_SUBGROUP))
            val mainGroup = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_MAINGROUP))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_CODE))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_KRNAME))
            val engName = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_ENGNAME))
            val spec = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_SPEC))
            val unit = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_UNIT))
            val price = cursor.getInt(cursor.getColumnIndex(COLUMN_TOOL_PRICE))
            val replacementCycle = cursor.getInt(cursor.getColumnIndex(COLUMN_TOOL_REPLACEMENTCYCLE))
            val buyCode = cursor.getString(cursor.getColumnIndex(COLUMN_TOOL_BUYCODE))

            val tool = ToolDtoSQLite(id, subGroup, mainGroup, code, name, engName, spec, unit, price, replacementCycle, buyCode)
            toolList.add(tool)
        }

        cursor.close()
        db.close()

        return toolList
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
    fun getAllStandby(): List<Any> {
        val gson = Gson()
        val db = this.readableDatabase
        val sheetList = mutableListOf<Any>()
        val query = "SELECT $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ?"
        val selectionArgs = arrayOf("STANDBY")

        val cursor = db.rawQuery(query, selectionArgs)

        while (cursor.moveToNext()) {
            //val id = cursor.getInt(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            //val type = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_TYPE))
            //val status = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_STATUS))
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = removeFirstAndLastQuotes(json)
            Log.d("dbdb",json)
            try {
                val rentalRequestSheetApprove: RentalRequestSheetApprove = gson.fromJson(json, RentalRequestSheetApprove::class.java)
                sheetList.add(rentalRequestSheetApprove)
            } catch (e: Exception) {
                Log.d("db","cannot be rentalRequestSheet")
                try {
                    val returnSheetFormDto: ReturnSheetFormDto = gson.fromJson(json, ReturnSheetFormDto::class.java)
                    sheetList.add(returnSheetFormDto)
                } catch (e: Exception) {
                    Log.d("db","cannot be ReturnSheetForm")
                }
            }
        }

        cursor.close()
        db.close()

        return sheetList
    }

    @SuppressLint("Range")
    fun getRentalStandby(): List<String> {
        val sheetList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ? AND $COLUMN_STANDBY_TYPE = ?"

        val selectionArgs = arrayOf("STANDBY", "RENTAL")


        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = removeFirstAndLastQuotes(json)
            sheetList.add(json)
        }

        cursor.close()
        db.close()
        return sheetList
    }
    @SuppressLint("Range")
    fun getReturnStandby(): List<String> {
        val sheetList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ? AND $COLUMN_STANDBY_TYPE = ?"
        val selectionArgs = arrayOf("STANDBY", "RETURN")

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = removeFirstAndLastQuotes(json)
            sheetList.add(json)
        }

        cursor.close()
        db.close()
        return sheetList
    }
    fun removeFirstAndLastQuotes(input: String): String {
        return if (input.length >= 2 && input.first() == '"' && input.last() == '"') {
            input.substring(1, input.length - 1)
        } else {
            input
        }
    }
}
