package com.example.familytree.network.contributor

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContributorRequest(
    val usernames: List<String>
)