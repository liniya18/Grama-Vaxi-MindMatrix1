package com.example.gramavaximindmatrix

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccinations",
    foreignKeys = [
        ForeignKey(
            entity = AnimalEntity::class,
            parentColumns = ["id"],
            childColumns = ["animalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VaccinationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val animalId: Int,
    val vaccineName: String,
    val date: String,
    val nextDueDate: String
)
