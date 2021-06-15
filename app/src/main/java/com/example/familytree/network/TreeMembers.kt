package com.example.familytree.network

import com.example.familytree.network.member.Member
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TreeMembers(
    val id: Int,
    val name: String,
    val description: String?,
    val publicMode: Boolean?,
    val people: List<Member>
)