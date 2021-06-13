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
    val gender: Int?,
    val dateOfBirth: String?,
    val createdDate: String,
    val updatedDate: String?
) {
    override fun equals(other: Any?): Boolean {
        val otherUser = other as User
        return this.id == otherUser.id
    }

    val isMale
        get() = when (gender) {
            0 -> true
            else -> false
        }
}

data class ContributorList(
    val owner: User,
    val editors: List<User>?,
    var owned: Boolean?
)

data class Memory(
    val id: Int?,
    val familyTreeId: Int?,
    val description: String?,
    val memoryDate: String?,
    val imageUrls: List<String>?,
    val dateCreated: String?
)

data class AuthData(
    val userID: String,
    val accessToken: String,
    val refreshToken: String
)