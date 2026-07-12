package com.sakreenshot.app.ui.screens.detail

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sakreenshot.app.SakreenShotApplication
import com.sakreenshot.app.data.db.ScreenshotEntity
import com.sakreenshot.app.data.repository.DataRepository
import com.sakreenshot.app.theme.BeigeSurfaceElevated
import com.sakreenshot.app.theme.TextPrimary
import com.sakreenshot.app.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DataRepository) : ViewModel() {
    private val _screenshot = MutableStateFlow<ScreenshotEntity?>(null)
    val screenshot: StateFlow<ScreenshotEntity?> = _screenshot.asStateFlow()

    fun loadScreenshot(id: Long) {
        viewModelScope.launch {
            // Need a DAO query by ID. Wait, I only have findByMediaStoreId or I can observeAll and filter.
            // Let's add findById in DAO later if we need it, but for now we can just observeAll and find it.
            repository.observeAll().collect { list ->
                _screenshot.value = list.find { it.id == id }
            }
        }
    }
}

class DetailViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(screenshotId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as SakreenShotApplication
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModelFactory(app.repository))

    val screenshot by viewModel.screenshot.collectAsState()

    LaunchedEffect(screenshotId) {
        viewModel.loadScreenshot(screenshotId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenshot?.primaryCategory ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        screenshot?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(item.contentUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Screenshot",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BeigeSurfaceElevated)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (item.extractedText.isNotBlank()) {
                    Text(
                        text = "Extracted Text",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = BeigeSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.extractedText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Text("No text found in this screenshot.", color = TextSecondary)
                }
            }
        }
    }
}