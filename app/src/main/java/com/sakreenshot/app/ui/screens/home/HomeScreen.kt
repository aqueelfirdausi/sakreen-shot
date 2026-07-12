package com.sakreenshot.app.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sakreenshot.app.SakreenShotApplication
import com.sakreenshot.app.data.db.ScreenshotEntity
import com.sakreenshot.app.theme.BeigeSurfaceElevated
import com.sakreenshot.app.theme.TextPrimary
import com.sakreenshot.app.theme.TextSecondary
import com.sakreenshot.app.ui.permissions.PermissionRequester
import com.sakreenshot.app.worker.WorkManagerHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCleanup: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as SakreenShotApplication
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(app.repository))
    val screenshots by viewModel.screenshots.collectAsState()
    val categoryCounts by viewModel.categoryCounts.collectAsState()
    val pinnedScreenshots by viewModel.pinnedScreenshots.collectAsState()

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showPermissionUI by remember { mutableStateOf(true) }
    var permissionGranted by remember { mutableStateOf(false) }

    if (showPermissionUI) {
        PermissionRequester(
            onPermissionGranted = {
                permissionGranted = true
                showPermissionUI = false
                WorkManagerHelper.scheduleSync(context)
                WorkManagerHelper.scheduleObserver(context)
            },
            onPermissionDenied = {
                showPermissionUI = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sakreen Shot", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    TextButton(onClick = onNavigateToSearch) {
                        Text("Search", color = TextPrimary)
                    }
                    TextButton(onClick = onNavigateToCleanup) {
                        Text("Clean", color = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        if (!permissionGranted) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "Sakreen Shot requires storage access to detect and organize your screenshots.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else if (screenshots.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Analyzing screenshots...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                if (categoryCounts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            lazyRowItems(categoryCounts) { count ->
                                Surface(
                                    color = if (selectedCategory == count.primaryCategory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.clickable { selectedCategory = count.primaryCategory }
                                ) {
                                    Text(
                                        text = "${count.primaryCategory} (${count.count})",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selectedCategory == count.primaryCategory) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            item {
                                if (selectedCategory != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.clickable { selectedCategory = null }
                                    ) {
                                        Text(
                                            text = "Clear Filter",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val filteredPinned = pinnedScreenshots.filter { selectedCategory == null || it.primaryCategory == selectedCategory }
                val filteredRecent = screenshots.filter { !it.isPinned && (selectedCategory == null || it.primaryCategory == selectedCategory) }

                if (filteredPinned.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Pinned",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    items(filteredPinned, key = { "pinned_${it.id}" }) { item ->
                        ScreenshotCard(item, onClick = { onNavigateToDetail(item.id) })
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Recent",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(filteredRecent, key = { "recent_${it.id}" }) { item ->
                    ScreenshotCard(item, onClick = { onNavigateToDetail(item.id) })
                }
            }
        }
    }
}

@Composable
fun ScreenshotCard(item: ScreenshotEntity, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dateString = formatter.format(Date(item.capturedAt))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.56f) // Standard 9:16 phone ratio approximation
                .clip(RoundedCornerShape(12.dp))
                .background(BeigeSurfaceElevated)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(item.contentUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Screenshot from $dateString",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Category Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.primaryCategory,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dateString,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
        
        if (item.extractedText.isNotBlank()) {
            Text(
                text = item.extractedText.replace("\n", " "),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
