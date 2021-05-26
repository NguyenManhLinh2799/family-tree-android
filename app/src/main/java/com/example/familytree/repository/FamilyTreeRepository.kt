package com.example.familytree.repository

import android.util.Log
import com.example.familytree.database.FamilyTreeDatabase
import com.example.familytree.domain.AuthData
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.member.Member
import com.example.familytree.network.NetworkTree
import com.example.familytree.network.auth.LoginRequest
import com.example.familytree.network.member.AddChildMemberRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FamilyTreeRepository(private val database: FamilyTreeDatabase) {

    // Auth
    suspend fun login(usernameOrEmail: String, password: String) = withContext(Dispatchers.IO) {
        val response = FamilyTreeApi.retrofitService.login(LoginRequest(usernameOrEmail, password, true))

        val httpCode = response.code()
        val container = response.body()
        if (httpCode == 200) {
            database.authDataDao.insert(container!!.data.asDatabaseModel())
            return@withContext true
        } else {
            return@withContext false
        }
    }

    suspend fun getAuthData(): AuthData = withContext(Dispatchers.IO) {
        return@withContext database.authDataDao.getAuthData().asDomainModel()
    }

    // Tree
    suspend fun getAllTrees() = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        return@withContext FamilyTreeApi.retrofitService.getTrees("Bearer ${authData.accessToken}")
    }

    suspend fun addTree(name: String, description: String?) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addTree(
                "Bearer ${authData.accessToken}",
                NetworkTree(null, name, description, true, null, null)
            )
        }
    }

    suspend fun upadateTree(id: Int?, name: String, description: String?) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.editTree(
                id,
                "Bearer ${authData.accessToken}",
                NetworkTree(null, name, description, true, null, null)
            )
        }
    }

    suspend fun deleteTree(id: Int?) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.deleteTree(id, "Bearer ${authData.accessToken}")
        }
    }

    // Tree members
    suspend fun getTreeMembers(treeID: Int) = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        return@withContext FamilyTreeApi.retrofitService.getTreeMembers(treeID, "Bearer ${authData.accessToken}")
    }

    // Member
    suspend fun getMember(id: Int) = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        Log.e("FamilyTreeRepository", id.toString())
        return@withContext FamilyTreeApi.retrofitService.getPerson(id, "Bearer ${authData.accessToken}")
    }

    suspend fun addChildMember(fatherId: Int?, motherId: Int?, childInfo: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addChild(
                "Bearer ${authData.accessToken}",
                AddChildMemberRequest(fatherId, motherId, childInfo))
            Log.e("AddChild", fatherId.toString())
            Log.e("AddChild", motherId.toString())
            Log.e("AddChild", childInfo.fullName)
            Log.e("AddChild", childInfo.sex)
        }
    }

    suspend fun editMember(editedMember: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.editPerson(editedMember.id!!, "Bearer ${authData.accessToken}", editedMember)
        }
    }
}