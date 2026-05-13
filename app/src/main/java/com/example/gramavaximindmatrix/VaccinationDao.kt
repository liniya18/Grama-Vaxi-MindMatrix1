package com.example.gramavaximindmatrix

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccination(vaccination: VaccinationEntity)

    @Query("SELECT * FROM vaccinations WHERE animalId = :animalId")
    fun getVaccinationsForAnimal(animalId: Int): Flow<List<VaccinationEntity>>

    @Query("SELECT * FROM vaccinations")
    fun getAllVaccinations(): Flow<List<VaccinationEntity>>
}
