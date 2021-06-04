package com.example.familytree.network.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EditProfileRequest(
    val firstName: String?,
    val midName: String?,
    val lastName: String?,
    var avatarUrl: String?,
    val address: String?,
    val phone: String?,
    val gender: Int?,
    val dateOfBirth: String?
)