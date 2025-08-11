package com.example.mvvm.components.topbar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mvvm.R
import com.example.mvvm.Screen
import com.example.mvvm.sample.WeatherDisplay
import com.example.mvvm.ui.theme.Purple40
import com.example.mvvm.utils.LanguageManager
import com.example.mvvm.sample.WeatherViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavHostController,
    isScrolled: Boolean = false,
    modifier: Modifier = Modifier // Add modifier parameter
) {
    var selectedLanguage by remember { mutableStateOf("Vietnamese") }
    val context = LocalContext.current

    // Weather data - would come from ViewModel in real app
    val weatherViewModel: WeatherViewModel = hiltViewModel<WeatherViewModel>()

    LaunchedEffect(Unit) {
        selectedLanguage = LanguageManager.getCurrentLanguageName(context)
    }

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherDisplay(
                    viewModel = weatherViewModel,
                    isScrolled = isScrolled
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Thông báo",
                            // Show notification icon in white when not scrolled for better visibility over image
                            tint = Color.White
                        )
                    }
                    LanguageButton(
                        isScrolled = isScrolled,
                        onClick = {
                            navController.navigate(Screen.ChangeLanguage.route)
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // Use semi-transparent background when not scrolled for floating effect
            containerColor = if (isScrolled) {
                Purple40
            } else {
                Color.Black.copy(alpha = 0.3f) // Semi-transparent overlay
            }
        ),
        modifier = modifier.then(
            if (isScrolled) Modifier.shadow(4.dp) else Modifier
        )
    )
}

@Composable
private fun LanguageButton(
    isScrolled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val vietnameseFlag = "\uD83C\uDDFB\uD83C\uDDF3"
    val englishFlag = "\uD83C\uDDFA\uD83C\uDDF8"

    // Get current language from LanguageManager
    val currentLang = LanguageManager.getLocale(context)
    val currentDisplayText = when (currentLang) {
        "vi" -> "${stringResource(R.string.vietnamese)}"
        "en" -> "${stringResource(R.string.english)}"
        else -> "${stringResource(R.string.vietnamese)}"
    }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Card(
            modifier = Modifier.clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (isScrolled) Color.White.copy(alpha = 0.9f) else Color.White.copy(
                    alpha = 0.8f
                )
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDisplayText,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.Language,
                    contentDescription = "Change Language",
                    tint = Color.Black,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, backgroundColor = 0xFF404137)
@Composable
fun HomeTopBarPreview() {
    HomeTopBar(
        navController = rememberNavController(),
        isScrolled = false,
        modifier = Modifier.fillMaxWidth()
    )
}