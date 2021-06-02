package com.example.familytree.network

import com.example.familytree.domain.Contributor
import com.example.familytree.domain.Tree
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkTreeListContainer(val data: List<NetworkTree>, val message: String, val errors: List<String>?)

@JsonClass(generateAdapter = true)
data class NetworkContributor(
    val id: String,
    val username: String,
    val avatarUrl: String?
) {
    fun asDomainModel(): Contributor {
        return Contributor(id, username, avatarUrl)
    }
}

fun List<NetworkContributor>.asDomainModel(): List<Contributor> {
    return map {
        Contributor(
            id = it.id,
            username = it.username,
            avatarUrl = it.avatarUrl
        )
    }
}

@JsonClass(generateAdapter = true)
data class NetworkTree(
    val id: Int?,
    val name: String,
    val description: String?,
    val publicMode: Boolean?,
    val owner: NetworkContributor?,
    val editors: List<NetworkContributor>?
)

fun NetworkTreeListContainer.asDomainModel(): List<Tree> {
    return data.map {
        Tree(
            id = it.id,
            name = it.name,
            description = it.description,
            publicMode = it.publicMode,
            owner = it.owner?.asDomainModel(),
            editors = it.editors?.asDomainModel()
        )
    }
}