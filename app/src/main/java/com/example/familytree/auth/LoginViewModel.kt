package com.example.familytree.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.familytree.database.getDatabase
import com.example.familytree.my_trees.MyTreesViewModel
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.auth.LoginRequest
import com.example.familytree.repository.FamilyTreeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel(context: Context): ViewModel() {

    lateinit var accessToken: String
    lateinit var refreshToken: String
    var loginState = MutableLiveData(0) // 0: not yet login, 1: success, -1: failed

    private val database = getDatabase(context)
    private val familyTreeRepository = FamilyTreeRepository(database)

    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            val success = familyTreeRepository.login(usernameOrEmail, password)

            if (success) {
                loginState.value = 1

                val authData = familyTreeRepository.getAuthData()
                Log.e("LoginViewModel", "userID: ${authData.userID}")
                Log.e("LoginViewModel", "accessToken: ${authData.accessToken}")
                Log.e("LoginViewModel", "refreshToken: ${authData.refreshToken}")
            } else {
                loginState.value = -1
            }
        }
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(context) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}