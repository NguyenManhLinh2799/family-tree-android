package com.example.familytree.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseTree constructor(
        @PrimaryKey
        val id: Int,
        val name: String,
        val description: String
)
