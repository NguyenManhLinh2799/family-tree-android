package com.example.familytree.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.familytree.domain.Tree

@Entity
data class DatabaseTree constructor(
    @PrimaryKey
    val id: Int?,
    val name: String,
    val description: String?
)

fun List<DatabaseTree>.asDomainModel(): List<Tree> {
    return map {
        Tree(
            id = it.id,
            name = it.name,
            description = it.description
        )
    }
}
