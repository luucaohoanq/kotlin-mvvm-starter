package com.example.mvvm.repositories

import com.example.mvvm.models.Camera
import com.example.mvvm.models.CameraType
import com.example.mvvm.utils.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface CameraRepository {
    fun getAllCameras(): Flow<List<Camera>>
    fun getCameraById(id: Int): Flow<Camera?>
    fun getCamerasByType(type: CameraType): Flow<List<Camera>>
    fun getFeaturedCameras(): Flow<List<Camera>>
}

@Singleton
class CameraRepositoryImpl @Inject constructor() : CameraRepository {
    
    override fun getAllCameras(): Flow<List<Camera>> = flow {
        emit(SampleData.cameras)
    }
    
    override fun getCameraById(id: Int): Flow<Camera?> = flow {
        emit(SampleData.getCameraById(id))
    }
    
    override fun getCamerasByType(type: CameraType): Flow<List<Camera>> = flow {
        emit(SampleData.getCamerasByType(type))
    }
    
    override fun getFeaturedCameras(): Flow<List<Camera>> = flow {
        emit(SampleData.getFeaturedCameras())
    }
}
