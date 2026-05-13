package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

data class ReportStats(
    val totalAnimals: Int = 0,
    val vaccinatedAnimals: Int = 0,
    val pendingVaccinations: Int = 0,
    val allAnimals: List<AnimalEntity> = emptyList(),
    val allVaccinations: List<VaccinationEntity> = emptyList()
)

class ReportsViewModel(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModel() {

    val stats: StateFlow<ReportStats> = combine(
        animalDao.getAllAnimals(),
        vaccinationDao.getAllVaccinations()
    ) { animals, vaccinations ->
        val total = animals.size
        val vaccinatedAnimalIds = vaccinations.map { it.animalId }.distinct()
        val vaccinatedCount = vaccinatedAnimalIds.size
        val pendingCount = total - vaccinatedCount
        
        ReportStats(
            totalAnimals = total,
            vaccinatedAnimals = vaccinatedCount,
            pendingVaccinations = if (pendingCount > 0) pendingCount else 0,
            allAnimals = animals,
            allVaccinations = vaccinations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportStats()
    )
}

class ReportsViewModelFactory(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(animalDao, vaccinationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
