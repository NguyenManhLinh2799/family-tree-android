package com.example.familytree.network

import android.os.Parcelable
import com.example.familytree.domain.Memory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkMemory(
    val id: Int?,
    val familyTreeId: Int?,
    val description: String?,
    val memoryDate: String?,
    var imageUrls: List<String>?,
    val dateCreated: String?
) : Parcelable {
    fun asDomainModel(): Memory {
        return Memory(id, familyTreeId, description, memoryDate, imageUrls, dateCreated)
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
            dateCreated = it.dateCreated
        )
    }
}