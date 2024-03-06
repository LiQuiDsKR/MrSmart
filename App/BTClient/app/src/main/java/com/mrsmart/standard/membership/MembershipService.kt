package com.mrsmart.standard.membership

import android.nfc.Tag
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.DatabaseHelper
import com.mrsmart.standard.page.Page

class MembershipService private constructor() {
    val dbHelper: DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()

    fun getMembershipById(id: Long): MembershipDto {
        try {
            val membershipEntity = dbHelper.getMembershipById(id)
            return membershipEntity.toMembershipDto()
        } catch (e: UninitializedPropertyAccessException) {
            // Log.e(TAG, "Database not initialized for ID: $id", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            // Log.e(TAG, "Error fetching membership by ID: $id", e)
            throw RuntimeException("Failed to fetch membership by ID: $id due to an unexpected error.", e)
        }
    }

    fun getMembershipByCode(code: String): MembershipDto {
        try {
            val membershipEntity = dbHelper.getMembershipByCode(code)
            Log.v(TAG,membershipEntity.toString())
            return membershipEntity.toMembershipDto()
        } catch (e: UninitializedPropertyAccessException) {
            Log.e(TAG, "Database not initialized for code: $code", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching membership by code: $code", e)
            throw RuntimeException("Failed to fetch membership by code: $code due to an unexpected error.", e)
        }
    }

    fun getMembershipPasswordByCode(code: String): String {
        try {
            val membershipEntity = dbHelper.getMembershipByCode(code)
            Log.v(TAG,membershipEntity.toString())
            return membershipEntity.password
        } catch (e: UninitializedPropertyAccessException) {
            Log.e(TAG, "Database not initialized for code: $code", e)
            throw IllegalStateException("Database has not been initialized.", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching membership by code: $code", e)
            throw RuntimeException("Failed to fetch membership by code: $code due to an unexpected error.", e)
        }
    }

    fun resetTable() {
        dbHelper.clearMembershipTable()
    }

    fun insertMembershipByPage(page: Page){
        try {

            val membershipListType = object : TypeToken<List<MembershipDto>>() {}.type
            val membershipList: List<MembershipDto> = gson.fromJson(gson.toJson(page.content), membershipListType)
            for (member in membershipList) {
                val id = member.id
                val code = member.code
                val password = member.password
                val name = member.name
                val part = member.partDto.name
                val subPart = member.partDto.subPartDto.name
                val mainPart = member.partDto.subPartDto.mainPartDto.name
                val role = member.role.toString()
                val employmentStatus = member.employmentStatus.toString()

                dbHelper.insertMembershipData(id, code, password, name, part, subPart, mainPart, role, employmentStatus)
                Log.d(TAG, "code : ${code}, name : ${name} inserted.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert membership data", e)
            throw Exception("Failed to insert membership data. Error: ${e.message}", e)
        }
    }

    fun upsertMembershipByPage(page: Page) {
        try {
            val membershipListType = object : TypeToken<List<MembershipDto>>() {}.type
            val membershipList: List<MembershipDto> = gson.fromJson(gson.toJson(page.content), membershipListType)
            for (member in membershipList) {
                val id = member.id
                val code = member.code
                val password = member.password
                val name = member.name
                val part = member.partDto.name
                val subPart = member.partDto.subPartDto.name
                val mainPart = member.partDto.subPartDto.mainPartDto.name
                val role = member.role.toString()
                val employmentStatus = member.employmentStatus.toString()

                dbHelper.upsertMembershipData(id, code, password, name, part, subPart, mainPart, role, employmentStatus)
                Log.d(TAG, "code : ${code}, name : ${name} upserted.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert membership data", e)
            throw Exception("Failed to insert membership data. Error: ${e.message}", e)
        }
    }

    companion object {
        private var instance: MembershipService? = null
        private const val TAG = "MembershipService"
        fun getInstance(): MembershipService {
            if (instance == null) {
                instance = MembershipService()
            }
            return instance!!
        }
    }
}