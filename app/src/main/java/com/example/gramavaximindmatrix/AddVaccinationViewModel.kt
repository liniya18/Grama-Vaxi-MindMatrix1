package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AddVaccinationUiState {
    object Idle : AddVaccinationUiState()
    object Loading : AddVaccinationUiState()
    object Success : AddVaccinationUiState()
    data class Error(val message: String) : AddVaccinationUiState()
}

class AddVaccinationViewModel(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddVaccinationUiState>(AddVaccinationUiState.Idle)
    val uiState: StateFlow<AddVaccinationUiState> = _uiState.asStateFlow()

    val animals: StateFlow<List<AnimalEntity>> = animalDao.getAllAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAnimalId = MutableStateFlow<Int?>(null)
    val selectedAnimalId = _selectedAnimalId.asStateFlow()

    private val _vaccineName = MutableStateFlow("")
    val vaccineName = _vaccineName.asStateFlow()

    private val _vaccinationDate = MutableStateFlow("")
    val vaccinationDate = _vaccinationDate.asStateFlow()

    private val _nextDueDate = MutableStateFlow("")
    val nextDueDate = _nextDueDate.asStateFlow()

    fun onAnimalSelected(id: Int) { _selectedAnimalId.value = id }
    fun onVaccineNameChange(name: String) { _vaccineName.value = name }
    fun onVaccinationDateChange(date: String) { _vaccinationDate.value = date }
    fun onNextDueDateChange(date: String) { _nextDueDate.value = date }

    fun saveVaccination() {
        val animalId = _selectedAnimalId.value
        val vaccine = _vaccineName.value
        val date = _vaccinationDate.value
        val nextDue = _nextDueDate.value

        if (animalId == null || vaccine.isBlank() || date.isBlank() || nextDue.isBlank()) {
            _uiState.value = AddVaccinationUiState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddVaccinationUiState.Loading
            try {
                vaccinationDao.insertVaccination(
                    VaccinationEntity(
                        animalId = animalId,
                        vaccineName = vaccine,
                        date = date,
                        nextDueDate = nextDue
                    )
                )
                _uiState.value = AddVaccinationUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddVaccinationUiState.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun resetState() { _uiState.value = AddVaccinationUiState.Idle }
}

class AddVaccinationViewModelFactory(
    private val animalDao: AnimalDao,
    private val vaccinationDao: VaccinationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVaccinationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddVaccinationViewModel(animalDao, vaccinationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
