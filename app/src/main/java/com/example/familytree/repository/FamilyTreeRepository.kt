package com.example.familytree.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.familytree.database.DatabaseAuthData
import com.example.familytree.database.DatabaseTree
import com.example.familytree.database.FamilyTreeDatabase
import com.example.familytree.database.asDomainModel
import com.example.familytree.domain.AuthData
import com.example.familytree.domain.Tree
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.NetworkTree
import com.example.familytree.network.asDatabaseModel
import com.example.familytree.network.auth.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FamilyTreeRepository(private val database: FamilyTreeDatabase) {

    // Auth
    suspend fun login(usernameOrEmail: String, password: String) {
        withContext(Dispatchers.IO) {
            val response = FamilyTreeApi.retrofitService.login(LoginRequest(usernameOrEmail, password, true))
            database.authDataDao.insert(response.data.asDatabaseModel())
        }
    }

    suspend fun getAuthData(): AuthData = withContext(Dispatchers.IO) {
        return@withContext database.authDataDao.getAuthData().asDomainModel()
    }

    // Tree
    val trees: LiveData<List<Tree>> = Transformations.map(database.treeDao.getTrees()) {
        it.asDomainModel()
    }

    suspend fun refreshTrees() {
        withContext(Dispatchers.IO) {
            val listTrees = FamilyTreeApi.retrofitService.getTrees()
            database.treeDao.insertAll(*listTrees.asDatabaseModel())
        }
    }

    suspend fun addTree(name: String, description: String?) {
        withContext(Dispatchers.IO) {
            FamilyTreeApi.retrofitService.addTree(NetworkTree(null, name, description))
            database.treeDao.insert(DatabaseTree(null, name, description))
        }
    }

    suspend fun upadateTree(id: Int?, name: String, description: String?) {
        withContext(Dispatchers.IO) {
            FamilyTreeApi.retrofitService.editTree(id, NetworkTree(null, name, description))
            database.treeDao.update(id, name, description)
        }
    }

    suspend fun deleteTree(id: Int?) {
        withContext(Dispatchers.IO) {
            FamilyTreeApi.retrofitService.deleteTree(id)
            database.treeDao.delete(id)
        }
    }
}