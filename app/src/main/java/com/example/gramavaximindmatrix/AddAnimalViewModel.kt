package com.example.gramavaximindmatrix

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed class AddAnimalUiState {
    object Idle : AddAnimalUiState()
    object Loading : AddAnimalUiState()
    object Success : AddAnimalUiState()
    data class Error(val message: String) : AddAnimalUiState()
}

class AddAnimalViewModel(private val animalDao: AnimalDao) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAnimalUiState>(AddAnimalUiState.Idle)
    val uiState: StateFlow<AddAnimalUiState> = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _species = MutableStateFlow("Cow")
    val species: StateFlow<String> = _species.asStateFlow()

    private val _breed = MutableStateFlow("")
    val breed: StateFlow<String> = _breed.asStateFlow()

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age.asStateFlow()

    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri: StateFlow<String?> = _imageUri.asStateFlow()

    fun onNameChange(newName: String) { _name.value = newName }
    fun onSpeciesChange(newSpecies: String) { _species.value = newSpecies }
    fun onBreedChange(newBreed: String) { _breed.value = newBreed }
    fun onAgeChange(newAge: String) { _age.value = newAge }
    fun onImageSelected(uri: String?) { _imageUri.value = uri }

    fun onImageCaptured(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            _uiState.value = AddAnimalUiState.Loading
            try {
                val savedUri = withContext(Dispatchers.IO) {
                    val file = File(context.cacheDir, "animal_${UUID.randomUUID()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    file.absolutePath
                }
                _imageUri.value = savedUri
                _uiState.value = AddAnimalUiState.Idle
            } catch (e: Exception) {
                _uiState.value = AddAnimalUiState.Error("Failed to save captured image")
            }
        }
    }

    fun saveAnimal() {
        val currentName = _name.value
        val currentSpecies = _species.value
        val currentBreed = _breed.value
        val currentAge = _age.value
        val currentImageUri = _imageUri.value

        if (currentName.isBlank() || currentSpecies.isBlank() || currentBreed.isBlank() || currentAge.isBlank()) {
            _uiState.value = AddAnimalUiState.Error("Please fill all required fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddAnimalUiState.Loading
            try {
                val animal = AnimalEntity(
                    name = currentName,
                    species = currentSpecies,
                    breed = currentBreed,
                    age = currentAge,
                    imageUri = currentImageUri
                )
                animalDao.insertAnimal(animal)
                _uiState.value = AddAnimalUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddAnimalUiState.Error(e.message ?: "Failed to save animal")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddAnimalUiState.Idle
    }
}

class AddAnimalViewModelFactory(private val animalDao: AnimalDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddAnimalViewModel(animalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
