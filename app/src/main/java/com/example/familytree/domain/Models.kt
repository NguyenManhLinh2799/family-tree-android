package com.example.familytree.domain

import com.example.familytree.network.auth.NetworkUser

data class Tree(
    val id: Int?,
    val name: String,
    val description: String?
)

data class User(
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

data class AuthData(
    val userID: String,
    val accessToken: String,
    val refreshToken: String
)