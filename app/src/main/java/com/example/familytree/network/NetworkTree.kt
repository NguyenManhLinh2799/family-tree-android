package com.example.familytree.network

import android.os.Parcelable
import com.example.familytree.database.DatabaseTree
import com.example.familytree.domain.Tree
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class NetworkTreeContainer(val data: List<NetworkTree>, val message: String, val errors: List<String>?)

@JsonClass(generateAdapter = true)
data class NetworkTree(
    val id: Int?,
    val name: String,
    val description: String?)

fun NetworkTreeContainer.asDomainModel(): List<Tree> {
    return data.map {
        Tree(
            id = it.id,
            name = it.name,
            description = it.description
        )
    }
}

fun NetworkTreeContainer.asDatabaseModel(): Array<DatabaseTree> {
    return data.map {
        DatabaseTree(
            id = it.id,
            name = it.name,
            description = it.description
        )
    }.toTypedArray()
}