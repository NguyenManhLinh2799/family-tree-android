package com.example.familytree.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.familytree.domain.AuthData
import com.example.familytree.domain.Tree

@Entity
data class DatabaseTree constructor(
    @PrimaryKey
    val id: Int?,
    val name: String,
    val description: String?
)

fun List<DatabaseTree>.asDomainModel(): List<Tree> {
    return map {
        Tree(
            id = it.id,
            name = it.name,
            description = it.description
        )
    }
}

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