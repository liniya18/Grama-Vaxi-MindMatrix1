package com.example.gramavaximindmatrix

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity): Long

    @Query("SELECT * FROM animals ORDER BY id DESC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>
}
