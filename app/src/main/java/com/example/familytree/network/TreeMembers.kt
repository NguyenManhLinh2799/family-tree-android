package com.example.familytree.network

import android.os.Parcelable
import com.example.familytree.network.member.Member
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TreeMembers(
    val id: Int,
    val name: String,
    val description: String?,
    val publicMode: Boolean?,
    val people: List<Member>
) : Parcelable