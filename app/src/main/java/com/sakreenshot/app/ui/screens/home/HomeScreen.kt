package com.sakreenshot.app.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sakreenshot.app.SakreenShotApplication
import com.sakreenshot.app.data.db.ScreenshotEntity
import com.sakreenshot.app.theme.AccentBronze
import com.sakreenshot.app.theme.BeigeBackground
import com.sakreenshot.app.theme.BeigeSurface
import com.sakreenshot.app.theme.BeigeSurfaceElevated
import com.sakreenshot.app.theme.BorderBronze
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
                title = {
                    Text(
                        text = "Sakreen Shot",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = AccentBronze)
                    }
                    IconButton(onClick = onNavigateToCleanup) {
                        Icon(Icons.Filled.CleaningServices, contentDescription = "Clean Up", tint = AccentBronze)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = AccentBronze)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BeigeBackground
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
                    CircularProgressIndicator(color = AccentBronze)
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
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                // Category Filter Bar
                if (categoryCounts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        ) {
                            lazyRowItems(categoryCounts) { count ->
                                val isSelected = selectedCategory == count.primaryCategory
                                Surface(
                                    color = if (isSelected) AccentBronze else BeigeSurface,
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, if (isSelected) AccentBronze else BorderBronze),
                                    modifier = Modifier.clickable(
                                        role = Role.Button,
                                        onClick = {
                                            selectedCategory = if (isSelected) null else count.primaryCategory
                                        }
                                    )
                                ) {
                                    Text(
                                        text = "${count.primaryCategory} (${count.count})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (isSelected) BeigeBackground else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                            item {
                                if (selectedCategory != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.clickable(
                                            role = Role.Button,
                                            onClick = { selectedCategory = null }
                                        )
                                    ) {
                                        Text(
                                            text = "Clear Filter",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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
                            text = "Pinned Records",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AccentBronze,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(filteredPinned, key = { "pinned_${it.id}" }) { item ->
                        ScreenshotCard(item, onClick = { onNavigateToDetail(item.id) })
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Recent Archive",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AccentBronze,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
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
    val cleanSnippet = remember(item.extractedText) {
        item.extractedText.replace("\\s+".toRegex(), " ").trim()
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BeigeSurfaceElevated),
        border = BorderStroke(1.dp, BorderBronze),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, role = Role.Button)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BeigeSurface)
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

                // Restrained Category Tag Badge
                Surface(
                    color = AccentBronze,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = item.primaryCategory,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BeigeBackground,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateString,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            
            if (cleanSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cleanSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
