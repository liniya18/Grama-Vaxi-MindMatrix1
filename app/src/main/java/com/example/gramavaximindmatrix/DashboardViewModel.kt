package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

enum class AlertType { OVERDUE, UPCOMING }

data class HealthAlert(
    val animalName: String,
    val vaccineName: String,
    val dueDate: String,
    val type: AlertType
)

data class DashboardStats(
    val totalAnimals: Int = 0,
    val vaccinatedToday: Int = 0,
    val upcomingAlerts: Int = 0
)

class DashboardViewModel(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao,
    private val campDao: CampDao
) : ViewModel() {

    val animals: StateFlow<List<AnimalEntity>> = animalDao.getAllAnimals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val healthAlerts: StateFlow<List<HealthAlert>> = combine(
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

        vaccinations.mapNotNull { vaccination ->
            try {
                val dueDate = sdf.parse(vaccination.nextDueDate) ?: return@mapNotNull null
                val animal = animals.find { it.id == vaccination.animalId }
                val animalName = animal?.name ?: "Unknown"

                when {
                    dueDate.before(today) -> HealthAlert(animalName, vaccination.vaccineName, vaccination.nextDueDate, AlertType.OVERDUE)
                    !dueDate.after(nextWeek) -> HealthAlert(animalName, vaccination.vaccineName, vaccination.nextDueDate, AlertType.UPCOMING)
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }.sortedWith(compareBy({ it.type }, { it.dueDate }))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stats: StateFlow<DashboardStats> = combine(
        animalDao.getAllAnimals(),
        vaccinationDao.getAllVaccinations(),
        healthAlerts
    ) { animals, vaccinations, alerts ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        
        DashboardStats(
            totalAnimals = animals.size,
            vaccinatedToday = vaccinations.count { it.date == todayStr },
            upcomingAlerts = alerts.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardStats()
    )

    val upcomingCamp: StateFlow<CampEntity?> = campDao.getAllCamps()
        .map { camps ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            camps.firstOrNull {
                try {
                    val campDate = sdf.parse(it.date)
                    campDate != null && (campDate.after(today) || campDate.equals(today))
                } catch (e: Exception) {
                    false
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}

class DashboardViewModelFactory(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao,
    private val campDao: CampDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(animalDao, vaccinationDao, campDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
