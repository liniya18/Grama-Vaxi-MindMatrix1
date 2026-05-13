package com.example.gramavaximindmatrix

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamp(camp: CampEntity)

    @Query("SELECT * FROM camps ORDER BY date ASC")
    fun getAllCamps(): Flow<List<CampEntity>>

    @Query("DELETE FROM camps WHERE id = :campId")
    suspend fun deleteCamp(campId: Int)
}
