package com.example.familytree.domain

data class Contributor(
    val id: String,
    val username: String,
    val avatarUrl: String?
)

data class Tree(
    val id: Int?,
    val name: String,
    val description: String?,
    val publicMode: Boolean?,
    val owner: Contributor?,
    val editors: List<Contributor>?
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
) {
    override fun equals(other: Any?): Boolean {
        val otherUser = other as User
        return this.id == otherUser.id
    }
}

data class ContributorList(
    val owner: User,
    val editors: List<User>?
)

data class AuthData(
    val userID: String,
    val accessToken: String,
    val refreshToken: String
)