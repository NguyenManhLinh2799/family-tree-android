package com.example.familytree.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familytree.network.FamilyTreeApi
import com.example.familytree.network.auth.NetworkAuthContainer
import com.example.familytree.network.auth.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {

    lateinit var accessToken: String
    lateinit var refreshToken: String

    fun register(
        username: String,
        email: String,
        password: String,
        phone: String,
        firstName: String,
        lastName: String,
        midName: String
    ) {
        viewModelScope.launch {
            val response = FamilyTreeApi.retrofitService.register(RegisterRequest(
                username,
                email,
                password,
                phone,
                firstName,
                lastName,
                midName,
                true
            ))
            accessToken = response.data.accessToken
            refreshToken = response.data.refreshToken
            Log.e("RegisterViewModel", response.message)
        }
    }
}