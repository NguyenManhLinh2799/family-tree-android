package com.example.familytree.network.member

import com.example.familytree.DateHelper
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Member(
    val spouses: List<Member>?,
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val dateOfDeath: String?,
    val parent1Id: Int?,
    val parent2Id: Int?,
    val gender: Int?,
    val phoneNumber: String?,
    val homeAddress: String?,
    val occupation: String?,
    val note: String?,
    val userId: String?,
    var imageUrl: String?
) {
    val fullName
        get() = "$firstName $lastName"

    val sex
        get() = when (gender) {
            0 -> "Male"
            else -> "Female"
        }

    val isMale
        get() = when (gender) {
            0 -> true
            else -> false
        }

    fun getLifeTime(): String {
        var dob = DateHelper.isoToDate(dateOfBirth)
        if (dob == "") {
            dob = "?"
        }
        var dod = DateHelper.isoToDate(dateOfDeath)
        if (dod == "") {
            dod = "?"
        }
        return "$dob - $dod"
    }
}

@JsonClass(generateAdapter = true)
data class MemberDetail(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val dateOfBirth: String?,
    val dateOfDeath: String?,
    val gender: Int?,
    val father: Member?,
    val mother: Member?,
    val spouses: List<Spouse>?,
    val children: List<Member>?,
    val phoneNumber: String?,
    val homeAddress: String?,
    val occupation: String?,
    val note: String?,
    val userId: String?,
    val imageUrl: String?
) {
    val fullName
        get() = "$firstName $lastName"

    val sex
        get() = when (gender) {
            0 -> "Male"
            else -> "Female"
        }

    val isMale
        get() = when (gender) {
            0 -> true
            else -> false
        }
}

@JsonClass(generateAdapter = true)
data class Spouse(
    val personSummary: Member?,
    val relationship: Relationship?
)

@JsonClass(generateAdapter = true)
data class Relationship(
    val id: Int?,
    val relationshipType: Int?,
    val startDate: String?,
    val endDate: String?
)