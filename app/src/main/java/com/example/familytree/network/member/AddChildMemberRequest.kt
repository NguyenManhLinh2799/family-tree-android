package com.example.familytree.network.member

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddChildMemberRequest(
    val fatherId: Int?,
    val motherId: Int?,
    val childInfo: Member
) : Parcelable