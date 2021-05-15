package com.example.familytree.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TreeMembers(
        val id: Int,
        val name: String,
        val description: String?,
        val people: List<Member>): Parcelable {

        }