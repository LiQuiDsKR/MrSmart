package com.liquidskr.btclient

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants.COLUMN_DEVICE_ID
import com.liquidskr.btclient.Constants.COLUMN_DEVICE_NAME
import com.liquidskr.btclient.Constants.COLUMN_Membership_CODE
import com.liquidskr.btclient.Constants.COLUMN_Membership_EMPLOYMENT_STATE
import com.liquidskr.btclient.Constants.COLUMN_Membership_ID
import com.liquidskr.btclient.Constants.COLUMN_Membership_MAINPART
import com.liquidskr.btclient.Constants.COLUMN_Membership_NAME
import com.liquidskr.btclient.Constants.COLUMN_Membership_PART
import com.liquidskr.btclient.Constants.COLUMN_Membership_PASSWORD
import com.liquidskr.btclient.Constants.COLUMN_Membership_ROLE
import com.liquidskr.btclient.Constants.COLUMN_Membership_SUBPART
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_ID
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_JSON
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_OUTSTANDINGCOUNT
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_RENTALSHEET
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_STATUS
import com.liquidskr.btclient.Constants.COLUMN_OUTSTANDING_TOTALCOUNT
import com.liquidskr.btclient.Constants.COLUMN_RENTALSHEET_ID
import com.liquidskr.btclient.Constants.COLUMN_RENTALSHEET_LEADER
import com.liquidskr.btclient.Constants.COLUMN_RENTALSHEET_TIMESTAMP
import com.liquidskr.btclient.Constants.COLUMN_RENTALSHEET_TOOLLIST
import com.liquidskr.btclient.Constants.COLUMN_RENTALSHEET_WOKRER
import com.liquidskr.btclient.Constants.COLUMN_STANDBY_DETAIL
import com.liquidskr.btclient.Constants.COLUMN_STANDBY_ID
import com.liquidskr.btclient.Constants.COLUMN_STANDBY_JSON
import com.liquidskr.btclient.Constants.COLUMN_STANDBY_STATUS
import com.liquidskr.btclient.Constants.COLUMN_STANDBY_TYPE
import com.liquidskr.btclient.Constants.COLUMN_TAG_ID
import com.liquidskr.btclient.Constants.COLUMN_TAG_MACADDRESS
import com.liquidskr.btclient.Constants.COLUMN_TAG_TAGGROUP
import com.liquidskr.btclient.Constants.COLUMN_TAG_TOOL_ID
import com.liquidskr.btclient.Constants.COLUMN_TBT_ID
import com.liquidskr.btclient.Constants.COLUMN_TBT_LOCATION
import com.liquidskr.btclient.Constants.COLUMN_TBT_QRCODE
import com.liquidskr.btclient.Constants.COLUMN_TBT_TOOLBOX_ID
import com.liquidskr.btclient.Constants.COLUMN_TBT_TOOL_ID
import com.liquidskr.btclient.Constants.COLUMN_TOOLBOX_ID
import com.liquidskr.btclient.Constants.COLUMN_TOOLBOX_NAME
import com.liquidskr.btclient.Constants.COLUMN_TOOLBOX_TOOLBOX_ID
import com.liquidskr.btclient.Constants.COLUMN_TOOL_BUYCODE
import com.liquidskr.btclient.Constants.COLUMN_TOOL_CODE
import com.liquidskr.btclient.Constants.COLUMN_TOOL_ENGNAME
import com.liquidskr.btclient.Constants.COLUMN_TOOL_ID
import com.liquidskr.btclient.Constants.COLUMN_TOOL_KRNAME
import com.liquidskr.btclient.Constants.COLUMN_TOOL_MAINGROUP
import com.liquidskr.btclient.Constants.COLUMN_TOOL_PRICE
import com.liquidskr.btclient.Constants.COLUMN_TOOL_REPLACEMENTCYCLE
import com.liquidskr.btclient.Constants.COLUMN_TOOL_SPEC
import com.liquidskr.btclient.Constants.COLUMN_TOOL_SUBGROUP
import com.liquidskr.btclient.Constants.COLUMN_TOOL_UNIT
import com.liquidskr.btclient.Constants.DATABASE_NAME
import com.liquidskr.btclient.Constants.DATABASE_VERSION
import com.liquidskr.btclient.Constants.TABLE_DEVICE_NAME
import com.liquidskr.btclient.Constants.TABLE_Membership_NAME
import com.liquidskr.btclient.Constants.TABLE_OUTSTANDING_NAME
import com.liquidskr.btclient.Constants.TABLE_RENTALSHEET_NAME
import com.liquidskr.btclient.Constants.TABLE_STANDBY_NAME
import com.liquidskr.btclient.Constants.TABLE_TAG_NAME
import com.liquidskr.btclient.Constants.TABLE_TBT_NAME
import com.liquidskr.btclient.Constants.TABLE_TOOLBOX_NAME
import com.liquidskr.btclient.Constants.TABLE_TOOL_NAME
import com.mrsmart.standard.membership.MembershipSQLite
import com.mrsmart.standard.rental.OutstandingRentalSheetDto
import com.mrsmart.standard.standby.StandbyDto
import com.mrsmart.standard.returns.ReturnToolFormDto
import com.mrsmart.standard.tool.ToolDtoSQLite
import java.lang.reflect.Type

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    class DatabaseHelperInitializationException(message: String) : Exception(message)

    companion object {
        @Volatile
        private var instance: DatabaseHelper? = null

        fun initInstance(context: Context) : DatabaseHelper {
            if (instance == null) {
                synchronized(DatabaseHelper::class.java) {
                    if (instance == null) {
                        instance = DatabaseHelper(context.applicationContext)
                    }
                }
            }
            return getInstance()
        }

        fun getInstance(): DatabaseHelper {
            return instance ?: throw DatabaseHelperInitializationException("DatabaseHelper not initialized")
        }
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
                "$COLUMN_STANDBY_STATUS TEXT, " +
                "$COLUMN_STANDBY_DETAIL TEXT)"

        val createTBTTableQuery = "CREATE TABLE $TABLE_TBT_NAME " +
                "($COLUMN_TBT_ID INTEGER, " +
                "$COLUMN_TBT_TOOLBOX_ID INTEGER, " +
                "$COLUMN_TBT_LOCATION TEXT, " +
                "$COLUMN_TBT_TOOL_ID INTEGER, " +
                "$COLUMN_TBT_QRCODE TEXT)"

        val createTagTableQuery = "CREATE TABLE $TABLE_TAG_NAME " +
                "($COLUMN_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TAG_MACADDRESS TEXT, " +
                "$COLUMN_TAG_TOOL_ID INTEGER, " +
                "$COLUMN_TAG_TAGGROUP TEXT)"

        val createRSTableQuery = "CREATE TABLE $TABLE_RENTALSHEET_NAME " +
                "($COLUMN_RENTALSHEET_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_RENTALSHEET_WOKRER TEXT, " +
                "$COLUMN_RENTALSHEET_LEADER TEXT, " +
                "$COLUMN_RENTALSHEET_TIMESTAMP TEXT, " +
                "$COLUMN_RENTALSHEET_TOOLLIST TEXT)"

        val createDeviceTableQuery = "CREATE TABLE $TABLE_DEVICE_NAME " +
                "($COLUMN_DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_DEVICE_NAME TEXT)"

        val createToolboxTableQuery = "CREATE TABLE $TABLE_TOOLBOX_NAME " +
                "($COLUMN_TOOLBOX_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TOOLBOX_TOOLBOX_ID TEXT, " +
                "$COLUMN_TOOLBOX_NAME TEXT)"

        val createOutstandingTableQuery = "CREATE TABLE $TABLE_OUTSTANDING_NAME " +
                "($COLUMN_OUTSTANDING_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_OUTSTANDING_RENTALSHEET TEXT, " +
                "$COLUMN_OUTSTANDING_TOTALCOUNT INTEGER, " +
                "$COLUMN_OUTSTANDING_OUTSTANDINGCOUNT INTEGER, " +
                "$COLUMN_OUTSTANDING_STATUS TEXT, " +
                "$COLUMN_OUTSTANDING_JSON TEXT)"

        db.execSQL(createMembershipTableQuery)
        db.execSQL(createToolTableQuery)
        db.execSQL(createStandbyTableQuery)
        db.execSQL(createTBTTableQuery)
        db.execSQL(createTagTableQuery)
        db.execSQL(createRSTableQuery)
        db.execSQL(createDeviceTableQuery)
        db.execSQL(createToolboxTableQuery)
        db.execSQL(createOutstandingTableQuery)
    }

    // 데이터베이스 도우미 클래스의 onUpgrade 메서드 내에서 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_Membership_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOL_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STANDBY_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TBT_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAG_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RENTALSHEET_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEVICE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOLBOX_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OUTSTANDING_NAME")

        onCreate(db)
    }

    fun refreshDeviceData(
        deviceName: String
    ): Long {

        val values = ContentValues()
        values.put(COLUMN_DEVICE_ID, 1)
        values.put(COLUMN_DEVICE_NAME, deviceName)

        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_DEVICE_NAME")
        } catch (e: Exception) {

        }
        val id = db.insert(TABLE_DEVICE_NAME, null, values)

        db.close()
        return id
    }

    fun refreshToolboxData(
        toolboxId: Long,
        toolboxName: String
    ): Long {

        val values = ContentValues()
        values.put(COLUMN_TOOLBOX_ID, 1)
        values.put(COLUMN_TOOLBOX_TOOLBOX_ID, toolboxId)
        values.put(COLUMN_TOOLBOX_NAME, toolboxName)

        val db = this.writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_TOOLBOX_NAME")
        } catch (e: Exception) {

        }
        val id = db.insert(TABLE_TOOLBOX_NAME, null, values)

        db.close()
        return id
    }
    @SuppressLint("Range")
    fun getToolboxName(): Long {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TOOLBOX_TOOLBOX_ID FROM $TABLE_TOOLBOX_NAME WHERE $COLUMN_TOOLBOX_ID = ?"
        var toolboxId: Long = 0
        val selectionArgs = arrayOf("1")
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            toolboxId = cursor.getLong(cursor.getColumnIndex(COLUMN_TOOLBOX_TOOLBOX_ID))
        }
        cursor.close()
        db.close()
        return toolboxId
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

    fun upsertMembershipData(membershipId: Long,
                             membershipCode: String,
                             membershipPassword: String,
                             membershipName: String,
                             membershipPart: String,
                             membershipSubpart: String,
                             membershipMainpart: String,
                             membershipRole: String,
                             membershipEmploymentState: String) : Long{
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

        val numberOfRowsUpdated = db.update(
            TABLE_Membership_NAME,
            values,
            "$COLUMN_Membership_ID = ?",
            arrayOf(membershipId.toString())
        )

        val id :Long
        if (numberOfRowsUpdated == 0) {
            id = db.insert(TABLE_Membership_NAME, null, values)
        } else {
            id = membershipId
        }

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
        standbyStatus: String,
        standbyDetail: String
    ): Long {
        val values = ContentValues()
        //values.put(COLUMN_STANDBY_ID, standbyId)
        values.put(COLUMN_STANDBY_JSON, standbyJson)
        values.put(COLUMN_STANDBY_TYPE, standbyType)
        values.put(COLUMN_STANDBY_STATUS, standbyStatus)
        values.put(COLUMN_STANDBY_DETAIL, standbyDetail)

        val db = this.writableDatabase
        val id = db.insert(TABLE_STANDBY_NAME, null, values)
        db.close()
        return id
    }
    fun insertTBTData(
        tbtId: Long,
        tbtToolboxId: Long,
        tbtLocation: String,
        tbtToolId: Long,
        tbtQRcode: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_TBT_ID, tbtId)
        values.put(COLUMN_TBT_TOOLBOX_ID, tbtToolboxId)
        values.put(COLUMN_TBT_LOCATION, tbtLocation)
        values.put(COLUMN_TBT_TOOL_ID, tbtToolId)
        values.put(COLUMN_TBT_QRCODE, tbtQRcode)

        val db = this.writableDatabase
        val id = db.insert(TABLE_TBT_NAME, null, values)
        db.close()
        return id
    }
    fun insertTagData(
        tagId: Long,
        tagMacAddress: String,
        tagToolId: Long,
        tagTagGroup: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_TAG_ID, tagId)
        values.put(COLUMN_TAG_MACADDRESS, tagMacAddress)
        values.put(COLUMN_TAG_TOOL_ID, tagToolId)
        values.put(COLUMN_TAG_TAGGROUP, tagTagGroup)

        val db = this.writableDatabase
        val id = db.insert(TABLE_TAG_NAME, null, values)
        db.close()
        return id
    }
    fun insertRSData(
        rsId: Long,
        rsWorkerName: String,
        rsLeaderName: String,
        rsTimeStamp: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_RENTALSHEET_ID, rsId)
        values.put(COLUMN_RENTALSHEET_WOKRER, rsWorkerName)
        values.put(COLUMN_RENTALSHEET_LEADER, rsLeaderName)
        values.put(COLUMN_RENTALSHEET_TIMESTAMP, rsTimeStamp)

        val db = this.writableDatabase
        val id = db.insert(TABLE_RENTALSHEET_NAME, null, values)
        db.close()
        return id
    }

    fun insertOutstandingData(
        orsId: Long,
        orsRentalSheet: String,
        orsTotalCount: Int,
        orsTotalOutstandingCount: Int,
        orsOutstandingStatus: String,
        orsJson: String
    ): Long {
        val values = ContentValues()
        values.put(COLUMN_OUTSTANDING_ID, orsId)
        values.put(COLUMN_OUTSTANDING_RENTALSHEET, orsRentalSheet)
        values.put(COLUMN_OUTSTANDING_TOTALCOUNT, orsTotalCount)
        values.put(COLUMN_OUTSTANDING_OUTSTANDINGCOUNT, orsTotalOutstandingCount)
        values.put(COLUMN_OUTSTANDING_STATUS, orsOutstandingStatus)
        values.put(COLUMN_OUTSTANDING_JSON, orsJson)

        val db = this.writableDatabase
        val id = db.insert(TABLE_OUTSTANDING_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getDeviceName(): String {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_DEVICE_NAME FROM $TABLE_DEVICE_NAME WHERE $COLUMN_DEVICE_ID = ?"
        var deviceName = ""
        val selectionArgs = arrayOf("1")
        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            deviceName = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_NAME))
        }
        cursor.close()
        db.close()
        return deviceName
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
    fun getMembershipById(codeToFind: Long): MembershipSQLite {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_Membership_ID, $COLUMN_Membership_NAME, $COLUMN_Membership_CODE, $COLUMN_Membership_PASSWORD, $COLUMN_Membership_PART, $COLUMN_Membership_SUBPART, $COLUMN_Membership_MAINPART, $COLUMN_Membership_ROLE, $COLUMN_Membership_EMPLOYMENT_STATE FROM $TABLE_Membership_NAME WHERE $COLUMN_Membership_ID = ?"
        val selectionArgs = arrayOf(codeToFind.toString())
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
    fun getMembershipIdByName(codeToFind: String): Long {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_Membership_ID FROM $TABLE_Membership_NAME WHERE $COLUMN_Membership_NAME = ?"
        val selectionArgs = arrayOf(codeToFind)
        var result: Long = 0

        val cursor = db.rawQuery(query, selectionArgs)
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_Membership_ID))
            result = id
        }

        cursor.close()
        db.close()
        return result
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
    fun getToolById(id: Long): ToolDtoSQLite {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TOOL_ID, $COLUMN_TOOL_SUBGROUP, $COLUMN_TOOL_MAINGROUP, $COLUMN_TOOL_CODE, $COLUMN_TOOL_KRNAME, $COLUMN_TOOL_ENGNAME, $COLUMN_TOOL_SPEC, $COLUMN_TOOL_UNIT, $COLUMN_TOOL_PRICE, $COLUMN_TOOL_REPLACEMENTCYCLE, $COLUMN_TOOL_BUYCODE FROM $TABLE_TOOL_NAME WHERE $COLUMN_TOOL_ID = ?"
        val selectionArgs = arrayOf(id.toString())
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
    fun getToolsByQueryByKrName(searchQuery: String): List<ToolDtoSQLite> {
        val toolList = mutableListOf<ToolDtoSQLite>()
        val db = this.readableDatabase
        val keywords = searchQuery.split(" ") // 검색어를 공백을 기준으로 분리

        val sqlConditions = mutableListOf<String>()
        for (keyword in keywords) {
            sqlConditions.add("$COLUMN_TOOL_KRNAME LIKE '%$keyword%'")
        }

        val query = "SELECT * FROM $TABLE_TOOL_NAME WHERE ${sqlConditions.joinToString(" OR ")}"

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
    fun getToolsByQueryByEngName(searchQuery: String): List<ToolDtoSQLite> {
        val toolList = mutableListOf<ToolDtoSQLite>()
        val db = this.readableDatabase
        val keywords = searchQuery.split(" ") // 검색어를 공백을 기준으로 분리

        val sqlConditions = mutableListOf<String>()
        for (keyword in keywords) {
            sqlConditions.add("$COLUMN_TOOL_ENGNAME LIKE '%$keyword%'")
        }

        val query = "SELECT * FROM $TABLE_TOOL_NAME WHERE ${sqlConditions.joinToString(" OR ")}"

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
    fun getToolsByQueryBySpec(searchQuery: String): List<ToolDtoSQLite> {
        val toolList = mutableListOf<ToolDtoSQLite>()
        val db = this.readableDatabase
        val keywords = searchQuery.split(" ") // 검색어를 공백을 기준으로 분리

        val sqlConditions = mutableListOf<String>()
        for (keyword in keywords) {
            sqlConditions.add("$COLUMN_TOOL_SPEC LIKE '%$keyword%'")
        }

        val query = "SELECT * FROM $TABLE_TOOL_NAME WHERE ${sqlConditions.joinToString(" OR ")}"

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
    fun getToolsByQuery(searchQuery: String): List<ToolDtoSQLite> {
        var toolList = mutableListOf<ToolDtoSQLite>()
        var krNameList = getToolsByQueryByKrName(searchQuery)
        var engNameList = getToolsByQueryByEngName(searchQuery)
        var specList = getToolsByQueryBySpec(searchQuery)

        toolList = krNameList.union(engNameList.union(specList)).toMutableList()

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
    fun getAllStandby(): List<StandbyDto> {
        val gson = Gson()
        val db = this.readableDatabase
        val sheetList = mutableListOf<StandbyDto>()
        val query = "SELECT $COLUMN_STANDBY_JSON, $COLUMN_STANDBY_TYPE, $COLUMN_STANDBY_DETAIL FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ?"
        val selectionArgs = arrayOf("STANDBY")

        val cursor = db.rawQuery(query, selectionArgs)

        while (cursor.moveToNext()) {
            //val id = cursor.getInt(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            val type = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_TYPE))
            val detail = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_DETAIL)) // workerName, leaderName, toolList
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = removeFirstAndLastQuotes(json)

            try {
                sheetList.add(StandbyDto(type, json, detail))
            } catch (e: Exception) {

            }
        }

        cursor.close()
        db.close()

        return sheetList
    }

    @SuppressLint("Range")
    fun getAllStandbyWithId(): List<Pair<Long, StandbyDto>> {
        val gson = Gson()
        val db = this.readableDatabase
        val sheetList = mutableListOf<Pair<Long, StandbyDto>>()
        val query = "SELECT $COLUMN_STANDBY_ID, $COLUMN_STANDBY_JSON, $COLUMN_STANDBY_TYPE, $COLUMN_STANDBY_DETAIL FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ?"
        val selectionArgs = arrayOf("STANDBY")

        val cursor = db.rawQuery(query, selectionArgs)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            val type = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_TYPE))
            val detail = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_DETAIL)) // workerName, leaderName, toolList
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = removeFirstAndLastQuotes(json)

            try {
                sheetList.add(Pair(id, StandbyDto(type, json, detail)))
            } catch (e: Exception) {

            }
        }

        cursor.close()
        db.close()

        return sheetList
    }

    @SuppressLint("Range")
    fun updateStandbyStatus(standbyId: Long) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_STANDBY_STATUS, "APPROVED")

        db.update(
            TABLE_STANDBY_NAME,
            contentValues,
            "$COLUMN_STANDBY_ID = ?",
            arrayOf(standbyId.toString())
        )

        db.close()
    }

    @SuppressLint("Range")
    fun getRentalStandby(): List<Pair<Long, String>> {
        val sheetList = mutableListOf<Pair<Long, String>>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_STANDBY_ID, $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ? AND $COLUMN_STANDBY_TYPE = ?"

        val selectionArgs = arrayOf("STANDBY", "RENTAL")

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            json = removeFirstAndLastQuotes(json)
            sheetList.add(Pair(id, json))
        }

        cursor.close()
        db.close()
        return sheetList
    }
    @SuppressLint("Range")
    fun getReturnStandby(): List<Pair<Long, String>> {
        val sheetList = mutableListOf<Pair<Long, String>>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_STANDBY_ID, $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ? AND $COLUMN_STANDBY_TYPE = ?"
        val selectionArgs = arrayOf("STANDBY", "RETURN")

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = json.replace("\\\"", "\"")
            json = removeFirstAndLastQuotes(json)
            sheetList.add(Pair(id, json))
        }

        cursor.close()
        db.close()
        return sheetList
    }

    @SuppressLint("Range")
    fun getRentalRequestStandby(): List<Pair<Long, String>> {
        val sheetList = mutableListOf<Pair<Long, String>>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_STANDBY_ID, $COLUMN_STANDBY_JSON FROM $TABLE_STANDBY_NAME WHERE $COLUMN_STANDBY_STATUS = ? AND $COLUMN_STANDBY_TYPE = ?"
        val selectionArgs = arrayOf("STANDBY", "RENTALREQUEST")

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_STANDBY_ID))
            var json = cursor.getString(cursor.getColumnIndex(COLUMN_STANDBY_JSON))
            json = json.replace("\\\"", "\"")
            json = json.replace("\\\\", "\\")
            json = json.replace("\\\"", "\"")
            json = removeFirstAndLastQuotes(json)
            sheetList.add(Pair(id, json))
        }

        cursor.close()
        db.close()
        return sheetList
    }


    @SuppressLint("Range")
    fun getToolByTBT(tbt: String): ToolDtoSQLite {
        var toolId: Long = 0
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TBT_TOOL_ID FROM $TABLE_TBT_NAME WHERE $COLUMN_TBT_QRCODE = ?"
        val selectionArgs = arrayOf(tbt)

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            toolId = cursor.getLong(cursor.getColumnIndex(COLUMN_TBT_TOOL_ID))
        }
        var toolDtoSQLite: ToolDtoSQLite = getToolById(toolId)

        cursor.close()
        db.close()
        return toolDtoSQLite
    }

    @SuppressLint("Range")
    fun getTBTByToolId(id: Long): String {
        var tbt = ""
        lateinit var toolDtoSQLite: ToolDtoSQLite
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TBT_QRCODE FROM $TABLE_TBT_NAME WHERE $COLUMN_TBT_TOOL_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            tbt = cursor.getString(cursor.getColumnIndex(COLUMN_TBT_QRCODE))
        }

        cursor.close()
        db.close()
        return tbt
    }
    @SuppressLint("Range")
    fun updateQRCodeById(toolid: Long, newQRCode: String, toolboxId: Long): Int {
        val values = ContentValues()
        values.put(COLUMN_TBT_QRCODE, newQRCode)

        val db = this.writableDatabase

        val rowsAffected = db.update(TABLE_TBT_NAME, values, "$COLUMN_TBT_TOOL_ID = ?", arrayOf(toolid.toString()))
        if (rowsAffected == 0) {
            insertTBTData(0, toolboxId, "",toolid, newQRCode)
        }
        db.close()
        return rowsAffected
    }

    fun updateOutstandingStatusBySheetId(sheetId: Long): Int {
        val values = ContentValues()
        values.put(COLUMN_OUTSTANDING_STATUS, "APPROVED")

        val db = this.writableDatabase

        val rowsAffected = db.update(TABLE_OUTSTANDING_NAME, values, "$COLUMN_OUTSTANDING_ID = ?", arrayOf(sheetId.toString()))

        db.close()
        return rowsAffected
    }

    fun addTBT(newQRCode: String): Long {
        val values = ContentValues()
        values.put(COLUMN_TBT_QRCODE, newQRCode)

        val db = this.writableDatabase
        val id = db.insert(TABLE_TBT_NAME, null, values)

        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getTagGroupByTag(tag: String): String {
        var tagGroup = ""
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_TAG_TAGGROUP FROM $TABLE_TAG_NAME WHERE $COLUMN_TAG_MACADDRESS = ?"
        val selectionArgs = arrayOf(tag)

        val cursor = db.rawQuery(query, selectionArgs)
        while (cursor.moveToNext()) {
            tagGroup = cursor.getString(cursor.getColumnIndex(COLUMN_TAG_TAGGROUP))
        }

        cursor.close()
        db.close()
        return tagGroup
    }

    @SuppressLint("Range")
    fun getToolByTag(tag: String): ToolDtoSQLite {
        var toolId = 0
        lateinit var tool: ToolDtoSQLite
        try {
            val db = this.readableDatabase
            val query = "SELECT $COLUMN_TAG_TOOL_ID FROM $TABLE_TAG_NAME WHERE $COLUMN_TAG_MACADDRESS = ?"
            val selectionArgs = arrayOf(tag)
            val cursor = db.rawQuery(query, selectionArgs)
            while (cursor.moveToNext()) {
                toolId = cursor.getInt(cursor.getColumnIndex(COLUMN_TAG_TOOL_ID))
            }
            cursor.close()
            db.close()
        } catch (e:Exception) {
            //Toast.makeText(context,"QR 태그 목록을 조회하는데 실패했습니다.",Toast.LENGTH_SHORT).show()
            Log.i("dbHelper", "QR 태그 목록을 조회하는데 실패했습니다.")
        }

        try {
            tool =  getToolById(toolId.toLong())
        } catch (e:Exception) {
            //Toast.makeText(context,"공기구 목록을 조회하는데 실패했습니다.",Toast.LENGTH_SHORT).show()
            Log.i("dbHelper", "공기구 목록을 조회하는데 실패했습니다.")
        }
        return tool
    }
    @SuppressLint("Range")
    fun getNamesByRSId(id: Long): Pair<String, String> {
        var workerName = ""
        var leaderName = ""
        try {
            val db = this.readableDatabase
            val query = "SELECT $COLUMN_RENTALSHEET_WOKRER, $COLUMN_RENTALSHEET_LEADER FROM $TABLE_RENTALSHEET_NAME WHERE $COLUMN_RENTALSHEET_ID = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.rawQuery(query, selectionArgs)
            while (cursor.moveToNext()) {
                workerName = cursor.getString(cursor.getColumnIndex(COLUMN_RENTALSHEET_WOKRER))
                leaderName = cursor.getString(cursor.getColumnIndex(COLUMN_RENTALSHEET_LEADER))
            }
            cursor.close()
            db.close()
        } catch (e:Exception) {

        }

        return Pair(workerName, leaderName)
    }
    @SuppressLint("Range")
    fun getTimestampByRSId(id: Long): String {
        var timestamp = ""
        try {
            val db = this.readableDatabase
            val query = "SELECT $COLUMN_RENTALSHEET_TIMESTAMP FROM $TABLE_RENTALSHEET_NAME WHERE $COLUMN_RENTALSHEET_ID = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.rawQuery(query, selectionArgs)
            while (cursor.moveToNext()) {
                timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_RENTALSHEET_TIMESTAMP))
            }
            cursor.close()
            db.close()
        } catch (e:Exception) {

        }

        return timestamp
    }
    @SuppressLint("Range")
    fun getToolListByRSId(id: Long): List<ReturnToolFormDto> {
        val gson = Gson()
        var toolList = listOf<ReturnToolFormDto>()
        try {
            val db = this.readableDatabase
            val query = "SELECT $COLUMN_RENTALSHEET_TOOLLIST FROM $TABLE_RENTALSHEET_NAME WHERE $COLUMN_RENTALSHEET_ID = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.rawQuery(query, selectionArgs)
            while (cursor.moveToNext()) {
                val toolListString = cursor.getString(cursor.getColumnIndex(COLUMN_RENTALSHEET_TOOLLIST))
                val listReturnToolFormDtoType: Type = object : TypeToken<List<ReturnToolFormDto>>() {}.type
                toolList = gson.fromJson(toolListString, listReturnToolFormDtoType)
            }
            cursor.close()
            db.close()
        } catch (e:Exception) {

        }

        return toolList
    }

    @SuppressLint("Range")
    fun getAllOutstanding():List<OutstandingRentalSheetDto> {
        var outstandingList: MutableList<OutstandingRentalSheetDto> = mutableListOf()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_OUTSTANDING_JSON FROM $TABLE_OUTSTANDING_NAME WHERE $COLUMN_OUTSTANDING_STATUS = ?"

        val cursor = db.rawQuery(query, arrayOf("READY"))
        while (cursor.moveToNext()) {
            val json = cursor.getString(cursor.getColumnIndex(COLUMN_OUTSTANDING_JSON))
            val gson = Gson()
            val type: Type = object : TypeToken<OutstandingRentalSheetDto>() {}.type
            outstandingList.add(gson.fromJson(json, type))
        }

        cursor.close()
        db.close()
        return outstandingList
    }

    fun removeFirstAndLastQuotes(input: String): String {
        return if (input.length >= 2 && input.first() == '"' && input.last() == '"') {
            input.substring(1, input.length - 1)
        } else {
            input
        }
    }

    fun clearTBTTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_TBT_NAME")
        db.close()
    }

    fun clearMembershipTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_Membership_NAME")
        db.close()
    }
    fun clearToolTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_TOOL_NAME")
        db.close()
    }
    fun clearTagTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_TAG_NAME")
        db.close()
    }

    fun clearRSTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_RENTALSHEET_NAME")
        db.close()
    }

    fun clearStandbyTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_STANDBY_NAME")
        db.close()
    }

    fun clearOutstandingTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_OUTSTANDING_NAME")
        db.close()
    }
}
