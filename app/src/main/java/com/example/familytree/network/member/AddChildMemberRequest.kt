package com.example.familytree.network.member

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddChildMemberRequest(
    val fatherId: Int?,
    val motherId: Int?,
    val childInfo: Member
)