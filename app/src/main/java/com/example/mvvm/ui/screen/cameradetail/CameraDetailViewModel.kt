package com.example.mvvm.ui.screen.cameradetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm.models.Camera
import com.example.mvvm.repositories.CameraRepository
import com.example.mvvm.repositories.MainLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraDetailUiState(
    val camera: Camera? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CameraDetailViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val log: MainLog
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CameraDetailUiState())
    val uiState: StateFlow<CameraDetailUiState> = _uiState.asStateFlow()
    
    fun loadCamera(cameraId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                cameraRepository.getCameraById(cameraId)
                    .catch { exception ->
                        log.e("CameraDetailViewModel", "Error loading camera: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to load camera details: ${exception.message}"
                        )
                    }
                    .collect { camera ->
                        if (camera != null) {
                            _uiState.value = _uiState.value.copy(
                                camera = camera,
                                isLoading = false,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Camera not found"
                            )
                        }
                    }
            } catch (e: Exception) {
                log.e("CameraDetailViewModel", "Unexpected error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error occurred"
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        log.i("CameraDetailViewModel", "onCleared")
    }
}
