package com.sakreenshot.app.ui.screens.cleanup

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
import com.sakreenshot.app.theme.ColorDelete
import com.sakreenshot.app.theme.TextPrimary
import com.sakreenshot.app.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CleanupViewModel(private val repository: DataRepository) : ViewModel() {

    private val _candidates = MutableStateFlow<List<ScreenshotEntity>>(emptyList())
    val candidates: StateFlow<List<ScreenshotEntity>> = _candidates.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    init {
        loadCandidates()
    }

    private fun loadCandidates() {
        viewModelScope.launch {
            // Find screenshots older than 30 days or categorized as UNSORTED
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            val results = repository.fetchCleanupCandidates(thirtyDaysAgo, System.currentTimeMillis())
            _candidates.value = results
        }
    }

    fun toggleSelection(id: Long) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedIds.value = current
    }

    fun selectAll() {
        _selectedIds.value = _candidates.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun removeDeletedItemsFromDb(deletedIds: List<Long>) {
        viewModelScope.launch {
            val entities = _candidates.value.filter { deletedIds.contains(it.id) }
            val mediaStoreIds = entities.map { it.mediaStoreId }
            repository.deleteByMediaStoreIds(mediaStoreIds)
            
            // Reload list and clear selection
            _selectedIds.value = emptySet()
            loadCandidates()
        }
    }
}

class CleanupViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CleanupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CleanupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanupScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as SakreenShotApplication
    val viewModel: CleanupViewModel = viewModel(factory = CleanupViewModelFactory(app.repository))

    val candidates by viewModel.candidates.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.removeDeletedItemsFromDb(selectedIds.toList())
        }
    }

    fun triggerDelete() {
        val selectedEntities = candidates.filter { selectedIds.contains(it.id) }
        val uris = selectedEntities.map { Uri.parse(it.contentUri) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender = MediaStore.createDeleteRequest(context.contentResolver, uris).intentSender
            deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        } else {
            try {
                for (uri in uris) {
                    context.contentResolver.delete(uri, null, null)
                }
                viewModel.removeDeletedItemsFromDb(selectedIds.toList())
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as? RecoverableSecurityException
                        ?: throw securityException
                    val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                    deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                } else {
                    throw securityException
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedIds.isEmpty()) "Cleanup" else "${selectedIds.size} Selected") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (candidates.isNotEmpty()) {
                        TextButton(onClick = {
                            if (selectedIds.size == candidates.size) viewModel.clearSelection()
                            else viewModel.selectAll()
                        }) {
                            Text(if (selectedIds.size == candidates.size) "Deselect All" else "Select All", color = TextPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (selectedIds.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { triggerDelete() },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorDelete),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete ${selectedIds.size} Items")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (candidates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No old or unsorted screenshots to clean up!", color = TextSecondary, textAlign = TextAlign.Center)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(candidates, key = { it.id }) { item ->
                    val isSelected = selectedIds.contains(item.id)
                    CleanupItemCard(
                        item = item,
                        isSelected = isSelected,
                        onClick = { viewModel.toggleSelection(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CleanupItemCard(item: ScreenshotEntity, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(0.56f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else BeigeSurfaceElevated)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(item.contentUri))
                .crossfade(true)
                .build(),
            contentDescription = "Screenshot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorDelete.copy(alpha = 0.4f))
            )
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}