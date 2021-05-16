package com.example.familytree.network.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkAuthContainer(val data: NetworkAuthData, val message: String, val errors: List<String>?)

@JsonClass(generateAdapter = true)
data class NetworkUser(
        val id: String,
        val userName: String,
        val email: String,
        val loginProvider: String?,
        val firstName: String?,
        val midName: String?,
        val lastName: String?,
        val avatarUrl: String?,
        val address: String?,
        val phone: String?,
        val gender: String?,
        val dateOfBirth: String?,
        val status: Int,
        val createdDate: String,
        val updatedDate: String?
)

@JsonClass(generateAdapter = true)
data class NetworkAuthData(
        val user: NetworkUser,
        val accessToken: String,
        val refreshToken: String
)