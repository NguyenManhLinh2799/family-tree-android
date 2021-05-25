package com.example.familytree.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.familytree.domain.AuthData

@Entity
data class DatabaseAuthData constructor(
    @PrimaryKey
    val userID: String,
    val accessToken: String,
    val refreshToken: String
) {
    fun asDomainModel(): AuthData {
        return AuthData(userID, accessToken, refreshToken)
    }
}