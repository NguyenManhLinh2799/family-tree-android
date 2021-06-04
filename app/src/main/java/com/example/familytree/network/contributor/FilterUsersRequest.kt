package com.example.familytree.network.contributor

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FilterUsersRequest(
    val username: String?,
    val email: String?
) : Parcelable