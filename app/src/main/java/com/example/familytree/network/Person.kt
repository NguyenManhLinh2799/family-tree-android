package com.example.familytree.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Person(
        val spouses: List<Person>?,
        val id: Int,
        val firstName: String,
        val lastName: String,
        val dateOfBirth: String,
        val dateOfDeath: String?,
        val parent1Id: Int,
        val parent2Id: Int,
        val gender: Int,
        val note: String?,
        val userId: Int?) : Parcelable {

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