package com.example.familytree.network.contributor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FilterUsersRequest(
    val name: String?,
    val phone: String?,
    val gender: Int?,
    val bornBefore: String?,
    val usernameOrEmailContains: String?,
    val userName: String?,
    val email: String?
) : Parcelable