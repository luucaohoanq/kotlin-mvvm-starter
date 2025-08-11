package com.example.mvvm.ui.screen.cameras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm.models.Camera
import com.example.mvvm.repositories.CameraRepository
import com.example.mvvm.repositories.MainLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CamerasUiState(
    val cameras: List<Camera> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CamerasViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val log: MainLog
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CamerasUiState())
    val uiState: StateFlow<CamerasUiState> = _uiState.asStateFlow()
    
    init {
        loadCameras()
    }
    
    fun loadCameras() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                cameraRepository.getAllCameras()
                    .catch { exception ->
                        log.e("CamerasViewModel", "Error loading cameras: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to load cameras: ${exception.message}"
                        )
                    }
                    .collect { cameras ->
                        _uiState.value = _uiState.value.copy(
                            cameras = cameras,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                log.e("CamerasViewModel", "Unexpected error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error occurred"
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        log.i("CamerasViewModel", "onCleared")
    }
}
