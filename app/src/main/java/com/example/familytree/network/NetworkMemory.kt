package com.example.familytree.network

import android.os.Parcelable
import com.example.familytree.domain.Memory
import com.example.familytree.domain.User
import com.example.familytree.network.auth.NetworkUser
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class NetworkMemory(
    val id: Int?,
    val familyTreeId: Int?,
    val description: String?,
    val memoryDate: String?,
    var imageUrls: List<String>?,
    val dateCreated: String?,
    val creator: NetworkUser?
) {
    fun asDomainModel(): Memory {
        return Memory(id, familyTreeId, description, memoryDate, imageUrls, dateCreated, creator?.asDomainModel())
    }
}

fun List<NetworkMemory>.asDomainModel(): List<Memory> {
    return map {
        Memory(
            id = it.id,
            familyTreeId = it.familyTreeId,
            description = it.description,
            memoryDate = it.memoryDate,
            imageUrls = it.imageUrls,
            dateCreated = it.dateCreated,
            creator = it.creator?.asDomainModel()
        )
    }
}