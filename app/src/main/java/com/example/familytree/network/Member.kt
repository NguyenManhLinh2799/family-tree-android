package com.example.familytree.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Member(
        val spouses: List<Member>?,
        val id: Int,
        val firstName: String?,
        val lastName: String?,
        val dateOfBirth: String?,
        val dateOfDeath: String?,
        val parent1Id: Int?,
        val parent2Id: Int?,
        val gender: Int?,
        val note: String?,
        val userId: Int?) : Parcelable {

            val fullName
            get() = "$firstName $lastName"
            //get() = "$firstName"

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