package com.mrsmart.standard.membership

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liquidskr.btclient.Constants
import com.liquidskr.btclient.DatabaseHelper
import com.mrsmart.standard.page.Page
import java.lang.reflect.Type

class MembershipService (private val listener : Listener){

    interface Listener {
        fun onException(type : Constants.ExceptionType, description : String)
        fun onInserted(size : Int, index : Int, total : Int)
    }

    val dbHelper : DatabaseHelper by lazy { DatabaseHelper.getInstance() }
    val gson = Gson()
    fun getMembershipById(id : Long) : MembershipDto?{
        val membershipEntity : MembershipSQLite
        val membershipDto : MembershipDto
        try {
            membershipEntity = dbHelper.getMembershipById(id)
            membershipDto = membershipEntity.toMembership()
            return membershipDto
        }catch(e:UninitializedPropertyAccessException){
            listener.onException(Constants.ExceptionType.NO_QUERY_RESULT,"No Results (Membership) : id=${id}")
        }catch(e:Exception){
            listener.onException(Constants.ExceptionType.DATABASE_DEFAULT_EXCEPTION,e.toString())
        }
        return null
    }

    fun getMembershipByCode(code : String) : MembershipDto? {
        val membershipEntity : MembershipSQLite
        val membershipDto : MembershipDto
        try {
            membershipEntity = dbHelper.getMembershipByCode(code)
            membershipDto = membershipEntity.toMembership()
            return membershipDto
        }catch(e:UninitializedPropertyAccessException){
            listener.onException(Constants.ExceptionType.NO_QUERY_RESULT,"No Results (Membership) : code=${code}")
        }catch(e:Exception){
            listener.onException(Constants.ExceptionType.DATABASE_DEFAULT_EXCEPTION,e.toString())
        }
        return null
    }

    fun insertMembershipByPage(page : Page) {
        try {
            dbHelper.clearMembershipTable()
            val membershipListType: Type = object : TypeToken<List<MembershipDto>>() {}.type
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
                Log.d("membership", "code : ${code}, name : ${name} inserted.")
            }
            listener.onInserted(membershipList.size,page.pageable.page,page.total)
        } catch (e:Exception) {
            listener.onException(Constants.ExceptionType.DATABASE_INSERT_EXCEPTION,e.toString())
        }
    }

}