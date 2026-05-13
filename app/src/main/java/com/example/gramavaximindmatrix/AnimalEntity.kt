package com.example.gramavaximindmatrix

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val species: String,
    val breed: String,
    val age: String,
    val imageUri: String? = null
)
