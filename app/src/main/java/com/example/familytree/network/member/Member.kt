package com.example.familytree.network.member

import android.os.Parcelable
import com.example.familytree.DateHelper
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    val note: String?,
    val userId: String?,
    var imageUrl: String?
) : Parcelable {

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