package com.sakreenshot.app.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sakreenshot.app.theme.AccentBronze
import com.sakreenshot.app.theme.BeigeBackground
import com.sakreenshot.app.theme.TextPrimary
import com.sakreenshot.app.theme.TextSecondary
import com.sakreenshot.app.worker.WorkManagerHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    
    val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    val versionString = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "Version ${pInfo.versionName}"
        } catch (e: Exception) {
            "Version 1.0.0"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Privacy", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BeigeBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Storage Permission", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = AccentBronze)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Media Access Status: ${if (hasPermission) "Granted" else "Denied"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            HorizontalDivider()

            Column {
                Text("Local Sync & Ingestion", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = AccentBronze)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { WorkManagerHelper.scheduleSync(context) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBronze)
                ) {
                    Text("Run Screenshot Ingestion", color = BeigeBackground)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { WorkManagerHelper.scheduleSync(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retry Failed OCR Items", color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Background Ingestion Limitation",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Android OS or device battery saver settings may occasionally delay background detection. Any missed screenshots are reconciled automatically when you open Sakreen Shot.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            HorizontalDivider()

            Column {
                Text("Storage Sanity Policy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = AccentBronze)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Clean Cleanup tool identifies screenshots older than 30 days for user-confirmed deletion.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "100% Local-First Privacy. Your screenshots and extracted text never leave this device.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = versionString,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AccentBronze,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}