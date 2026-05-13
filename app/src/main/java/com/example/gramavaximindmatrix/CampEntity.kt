package com.example.gramavaximindmatrix

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "camps")
data class CampEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val location: String,
    val date: String,
    val description: String? = null
)
