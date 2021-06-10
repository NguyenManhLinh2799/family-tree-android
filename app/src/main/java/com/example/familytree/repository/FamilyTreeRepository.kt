package com.example.familytree.repository

import android.net.Uri
import android.util.Log
import com.example.familytree.database.FamilyTreeDatabase
import com.example.familytree.domain.AuthData
import com.example.familytree.domain.Memory
import com.example.familytree.domain.Tree
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.NetworkMemory
import com.example.familytree.network.member.Member
import com.example.familytree.network.NetworkTree
import com.example.familytree.network.asDomainModel
import com.example.familytree.network.auth.EditProfileRequest
import com.example.familytree.network.auth.LoginRequest
import com.example.familytree.network.auth.asDomainModel
import com.example.familytree.network.contributor.ContributorRequest
import com.example.familytree.network.member.AddChildMemberRequest
import com.example.familytree.network.contributor.FilterUsersRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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

    // Memory
    suspend fun getMemories(treeID: Int): List<Memory> = withContext(Dispatchers.IO) {
        return@withContext FamilyTreeApi.retrofitService.getMemories(treeID).data.asDomainModel()
    }

    suspend fun postMemory(newMemory: NetworkMemory) {
        withContext(Dispatchers.IO) {
            FamilyTreeApi.retrofitService.postMemory(newMemory)
        }
    }

    // Tree
    suspend fun getMyTrees() = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        val allTrees = FamilyTreeApi.retrofitService.getTrees("Bearer ${authData.accessToken}").asDomainModel()
        val myTrees = ArrayList<Tree>(0)
        for (tree in allTrees) {
            if (tree.owner?.id == authData.userID) {
                myTrees.add(tree)
            }
        }
        return@withContext myTrees
    }

    suspend fun getSharedTrees() = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        val allTrees = FamilyTreeApi.retrofitService.getTrees("Bearer ${authData.accessToken}").asDomainModel()
        val sharedTrees = ArrayList<Tree>(0)
        for (tree in allTrees) {
            if (tree.owner?.id != authData.userID) {
                sharedTrees.add(tree)
            }
        }
        return@withContext sharedTrees
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

    // Tree contributors
    suspend fun getContributors(treeID: Int) = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        val contributorList = FamilyTreeApi.retrofitService.getEditors(treeID, "Bearer ${authData.accessToken}")
            .data.asDomainModel()
        contributorList.owned = contributorList.owner.id == authData.userID
        return@withContext contributorList
    }

    suspend fun getAllUsers() = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        return@withContext FamilyTreeApi.retrofitService.filterUsers(
            "Bearer ${authData.accessToken}",
            FilterUsersRequest(null, null)
        ).data.asDomainModel()
    }

    suspend fun addContributor(treeID: Int, username: String) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addEditors(treeID,
                "Bearer ${authData.accessToken}",
                ContributorRequest(listOf(username)))
        }
    }

    suspend fun removeContributor(treeID: Int, username: String) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.removeEditors(treeID,
                "Bearer ${authData.accessToken}",
                ContributorRequest(listOf(username)))
        }
    }

    // Member
    suspend fun getMember(id: Int) = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        Log.e("FamilyTreeRepository", id.toString())
        return@withContext FamilyTreeApi.retrofitService.getPerson(id, "Bearer ${authData.accessToken}")
    }

    suspend fun addParentMember(id: Int, parentMember: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addParent(id, "Bearer ${authData.accessToken}", parentMember)
        }
    }

    suspend fun addPartnerMember(id: Int, partnerMember: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addSpouse(id, "Bearer ${authData.accessToken}", partnerMember)
        }
    }

    suspend fun addChildMember(fatherId: Int?, motherId: Int?, childInfo: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.addChild(
                "Bearer ${authData.accessToken}",
                AddChildMemberRequest(fatherId, motherId, childInfo))
        }
    }

    suspend fun editMember(editedMember: Member) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.editPerson(editedMember.id!!, "Bearer ${authData.accessToken}", editedMember)
        }
    }

    suspend fun deleteMember(id: Int) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.deletePerson(id, "Bearer ${authData.accessToken}")
        }
    }

    // Image
    suspend fun uploadImage(imgUri: Uri) = withContext(Dispatchers.IO) {
        val imgFile = File(imgUri.path)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile)
        val body = MultipartBody.Part.createFormData("file", imgFile.name, requestFile)

        return@withContext FamilyTreeApi.retrofitService.uploadImage(body)
    }

    suspend fun uploadImages(allImageUris: List<Uri>) = withContext(Dispatchers.IO) {
        val parts = ArrayList<MultipartBody.Part>(0)
        for (imgUri in allImageUris) {
            val imgFile = File(imgUri.path)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile)
            val part = MultipartBody.Part.createFormData("file", imgFile.name, requestFile)
            parts.add(part)
        }
        return@withContext FamilyTreeApi.retrofitService.uploadImages(parts)
    }

    // User
    suspend fun getProfile() = withContext(Dispatchers.IO) {
        val authData = database.authDataDao.getAuthData()
        return@withContext FamilyTreeApi.retrofitService.getProfile("Bearer ${authData.accessToken}").data.asDomainModel()
    }

    suspend fun editProfile(editedProfile: EditProfileRequest) {
        withContext(Dispatchers.IO) {
            val authData = database.authDataDao.getAuthData()
            FamilyTreeApi.retrofitService.editProfile("Bearer ${authData.accessToken}", editedProfile)
        }
    }
}