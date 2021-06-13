package com.example.familytree.network.auth

import com.example.familytree.database.DatabaseAuthData
import com.example.familytree.domain.AuthData
import com.example.familytree.domain.User
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
    var avatarUrl: String?,
    val address: String?,
    val phone: String?,
    val gender: Int?,
    val dateOfBirth: String?,
    val createdDate: String,
    val updatedDate: String?
) {
    fun asDomainModel(): User {
        return User(
            id, userName, email, loginProvider, firstName, midName, lastName, avatarUrl, address, phone, gender, dateOfBirth, createdDate, updatedDate
        )
    }
}

fun List<NetworkUser>.asDomainModel(): List<User> {
    return map {
        User(
            id = it.id,
            userName = it.userName,
            email = it.email,
            loginProvider = it.loginProvider,
            firstName = it.firstName,
            midName = it.midName,
            lastName = it.lastName,
            avatarUrl = it.avatarUrl,
            address = it.address,
            phone = it.phone,
            gender = it.gender,
            dateOfBirth = it.dateOfBirth,
            createdDate = it.createdDate,
            updatedDate = it.updatedDate
        )
    }
}

@JsonClass(generateAdapter = true)
data class NetworkAuthData(
    val user: NetworkUser,
    val accessToken: String,
    val refreshToken: String
) {
    fun asDomainModel(): AuthData {
        return AuthData(
            user.id,
            accessToken,
            refreshToken
        )
    }

    fun asDatabaseModel(): DatabaseAuthData {
        return DatabaseAuthData(
            user.id,
            accessToken,
            refreshToken
        )
    }
}