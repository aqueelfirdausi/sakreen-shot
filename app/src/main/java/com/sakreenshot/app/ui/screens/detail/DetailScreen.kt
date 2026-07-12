package com.sakreenshot.app.ui.screens.detail

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.sakreenshot.app.data.classification.Category
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
            repository.observeAll().collect { list ->
                _screenshot.value = list.find { it.id == id }
            }
        }
    }

    fun togglePin() {
        val current = _screenshot.value ?: return
        viewModelScope.launch {
            repository.updatePinState(current.id, !current.isPinned)
        }
    }

    fun updateCategory(category: Category) {
        val current = _screenshot.value ?: return
        viewModelScope.launch {
            repository.updateCategory(current.id, category.name)
        }
    }

    fun deleteFromDatabase(onDeleted: () -> Unit) {
        val current = _screenshot.value ?: return
        viewModelScope.launch {
            repository.delete(current)
            onDeleted()
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCategoryDialog by remember { mutableStateOf(false) }

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.deleteFromDatabase(onDeleted = onBack)
        }
    }

    fun triggerDelete() {
        val item = screenshot ?: return
        val uri = Uri.parse(item.contentUri)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender = MediaStore.createDeleteRequest(context.contentResolver, listOf(uri)).intentSender
            deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        } else {
            try {
                context.contentResolver.delete(uri, null, null)
                viewModel.deleteFromDatabase(onDeleted = onBack)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as? RecoverableSecurityException
                        ?: throw securityException
                    val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                    deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Unable to delete image") }
                }
            }
        }
    }

    fun shareImage() {
        val item = screenshot ?: return
        val uri = Uri.parse(item.contentUri)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Screenshot"))
    }

    LaunchedEffect(screenshotId) {
        viewModel.loadScreenshot(screenshotId)
    }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Change Category") },
            text = {
                Column {
                    Category.entries.forEach { cat ->
                        Text(
                            text = cat.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCategory(cat)
                                    showCategoryDialog = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(screenshot?.primaryCategory ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    screenshot?.let { item ->
                        IconButton(onClick = { viewModel.togglePin() }) {
                            Icon(
                                if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (item.isPinned) "Unpin" else "Pin"
                            )
                        }
                        IconButton(onClick = { shareImage() }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { triggerDelete() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showCategoryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Category: ${item.primaryCategory}", color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (item.extractedText.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Extracted Text",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Extracted Text", item.extractedText))
                            scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy text")
                        }
                    }
                    
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