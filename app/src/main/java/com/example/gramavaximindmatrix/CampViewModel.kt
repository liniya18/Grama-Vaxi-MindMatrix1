package com.example.gramavaximindmatrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AddCampUiState {
    object Idle : AddCampUiState()
    object Loading : AddCampUiState()
    object Success : AddCampUiState()
    data class Error(val message: String) : AddCampUiState()
}

class CampViewModel(private val campDao: CampDao) : ViewModel() {

    private val _uiState = MutableStateFlow<AddCampUiState>(AddCampUiState.Idle)
    val uiState: StateFlow<AddCampUiState> = _uiState.asStateFlow()

    val camps: StateFlow<List<CampEntity>> = campDao.getAllCamps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    private val _date = MutableStateFlow("")
    val date = _date.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    fun onTitleChange(newTitle: String) { _title.value = newTitle }
    fun onLocationChange(newLocation: String) { _location.value = newLocation }
    fun onDateChange(newDate: String) { _date.value = newDate }
    fun onDescriptionChange(newDesc: String) { _description.value = newDesc }

    fun saveCamp() {
        val currentTitle = _title.value
        val currentLocation = _location.value
        val currentDate = _date.value
        val currentDesc = _description.value

        if (currentTitle.isBlank() || currentLocation.isBlank() || currentDate.isBlank()) {
            _uiState.value = AddCampUiState.Error("Please fill all required fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddCampUiState.Loading
            try {
                campDao.insertCamp(
                    CampEntity(
                        title = currentTitle,
                        location = currentLocation,
                        date = currentDate,
                        description = if (currentDesc.isBlank()) null else currentDesc
                    )
                )
                _uiState.value = AddCampUiState.Success
                resetForm()
            } catch (e: Exception) {
                _uiState.value = AddCampUiState.Error(e.message ?: "Failed to save camp")
            }
        }
    }

    fun deleteCamp(campId: Int) {
        viewModelScope.launch {
            campDao.deleteCamp(campId)
        }
    }

    fun resetState() { _uiState.value = AddCampUiState.Idle }

    private fun resetForm() {
        _title.value = ""
        _location.value = ""
        _date.value = ""
        _description.value = ""
    }
}

class CampViewModelFactory(private val campDao: CampDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampViewModel(campDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
