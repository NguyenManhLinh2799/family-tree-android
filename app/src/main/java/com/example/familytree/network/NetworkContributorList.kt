package com.example.familytree.network

import com.example.familytree.domain.ContributorList
import com.example.familytree.domain.User
import com.example.familytree.network.auth.NetworkUser
import com.example.familytree.network.auth.asDomainModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkContributorListContainer(
    val data: NetworkContributorList,
    val message: String,
    val errors: List<String>?
)

@JsonClass(generateAdapter = true)
data class NetworkContributorList(val owner: NetworkUser, val editors: List<NetworkUser>?) {
    fun asDomainModel(): ContributorList {
        return ContributorList(
            owner.asDomainModel(),
            editors?.asDomainModel()
        )
    }
}