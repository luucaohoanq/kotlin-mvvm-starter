package com.example.mvvm.components.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvvm.models.Camera
import com.example.mvvm.utils.CurrencyManager

@Composable
fun CameraGrid(
    cameras: List<Camera>,
    onCameraClick: (Camera) -> Unit,
    currencyManager: CurrencyManager? = null,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cameras) { camera ->
            CameraCard(
                camera = camera,
                currencyManager = currencyManager,
                onClick = onCameraClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraGridPreview() {
    // Preview with empty list - actual data will come from ViewModel
    CameraGrid(
        cameras = emptyList(),
        onCameraClick = {}
    )
}
