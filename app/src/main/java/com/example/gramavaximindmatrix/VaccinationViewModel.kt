package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

enum class VaccinationStatus { OVERDUE, UPCOMING, SAFE }

data class VaccinationDisplayRecord(
    val id: Int,
    val animalName: String?,
    val vaccineName: String,
    val date: String,
    val nextDueDate: String,
    val status: VaccinationStatus
)

class VaccinationViewModel(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModel() {

    val vaccinationRecords: StateFlow<List<VaccinationDisplayRecord>> = combine(
        vaccinationDao.getAllVaccinations(),
        animalDao.getAllAnimals()
    ) { vaccinations, animals ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        val nextWeek = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_YEAR, 7)
        }.time

        vaccinations.map { vaccination ->
            val animal = animals.find { it.id == vaccination.animalId }
            
            val status = try {
                val dueDate = sdf.parse(vaccination.nextDueDate)
                if (dueDate == null) VaccinationStatus.SAFE
                else if (dueDate.before(today)) VaccinationStatus.OVERDUE
                else if (!dueDate.after(nextWeek)) VaccinationStatus.UPCOMING
                else VaccinationStatus.SAFE
            } catch (e: Exception) {
                VaccinationStatus.SAFE
            }

            VaccinationDisplayRecord(
                id = vaccination.id,
                animalName = animal?.name,
                vaccineName = vaccination.vaccineName,
                date = vaccination.date,
                nextDueDate = vaccination.nextDueDate,
                status = status
            )
        }.sortedBy { it.nextDueDate }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}

class VaccinationViewModelFactory(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VaccinationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VaccinationViewModel(animalDao, vaccinationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
