package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import com.example.data.sample.SampleData
import com.example.ui.components.JsonCodeViewer
import kotlinx.coroutines.launch

@Composable
fun ExportImportScreen(
    notes: List<Note>,
    repository: NoteRepository,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Export, 1: Import

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Mode Selector (Export vs Import)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export JSON", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier.testTag("tab_export")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import JSON", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier.testTag("tab_import")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            ExportContent(
                notes = notes,
                repository = repository,
                context = context
            )
        } else {
            ImportContent(
                repository = repository,
                context = context,
                onImportSuccess = onRefresh
            )
        }
    }
}

@Composable
private fun ExportContent(
    notes: List<Note>,
    repository: NoteRepository,
    context: Context
) {
    var exportOnlyFavorites by remember { mutableStateOf(false) }

    val notesToExport = remember(notes, exportOnlyFavorites) {
        if (exportOnlyFavorites) notes.filter { it.isFavorite } else notes
    }

    val jsonOutput = remember(notesToExport) {
        repository.exportToJson(notesToExport, indentSpaces = 2)
    }

    val byteSize = remember(jsonOutput) {
        jsonOutput.toByteArray().size
    }

    // Share JSON File launcher
    fun shareJsonFile() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, "VocabClip_Notes_Export.json")
            putExtra(Intent.EXTRA_TEXT, jsonOutput)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share / Download VocabClip Notes")
        context.startActivity(shareIntent)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cross-Device Sync & Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Export your entire vocabulary library or favorites as a portable JSON file. You can import this JSON onto another device, feed it into AI tools, or keep an offline backup.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${notesToExport.size}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Notes Ready",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val sizeKb = String.format("%.1f KB", byteSize / 1024.0)
                        Text(
                            text = sizeKb,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "JSON Payload",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Favorite filter checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { exportOnlyFavorites = !exportOnlyFavorites }
                        .padding(vertical = 4.dp)
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = exportOnlyFavorites,
                        onCheckedChange = { exportOnlyFavorites = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Export only Starred / Favorites",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VocabClip Export", jsonOutput))
                            Toast.makeText(context, "Copied ${notesToExport.size} notes as JSON to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_copy_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy JSON")
                    }

                    FilledTonalButton(
                        onClick = { shareJsonFile() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_share_file_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share / Save")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Code Viewer
        Text(
            text = "LIVE JSON EXPORT PREVIEW",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        JsonCodeViewer(
            code = jsonOutput,
            title = "VocabClip_Export.json (${notesToExport.size} notes)",
            maxHeight = 360
        )
    }
}

@Composable
private fun ImportContent(
    repository: NoteRepository,
    context: Context,
    onImportSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var rawJsonInput by remember { mutableStateOf("") }
    var replaceAll by remember { mutableStateOf(false) } // false = merge, true = replace
    var showConfirmReplaceDialog by remember { mutableStateOf(false) }

    // Parse status state
    var validationMessage by remember { mutableStateOf<String?>(null) }
    var isValid by remember { mutableStateOf<Boolean?>(null) }
    var detectedCount by remember { mutableIntStateOf(0) }

    // Real-time validation
    LaunchedEffect(rawJsonInput) {
        if (rawJsonInput.isBlank()) {
            validationMessage = null
            isValid = null
            detectedCount = 0
        } else {
            val result = repository.parseJsonNotes(rawJsonInput)
            result.onSuccess { list ->
                isValid = true
                detectedCount = list.size
                validationMessage = "Ready to import: ${list.size} notes detected!"
            }.onFailure { err ->
                isValid = false
                detectedCount = 0
                validationMessage = err.localizedMessage ?: "Invalid JSON syntax."
            }
        }
    }

    // File Picker for JSON file import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                if (content.isNotBlank()) {
                    rawJsonInput = content
                    Toast.makeText(context, "Loaded file successfully. Validating...", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun executeImport() {
        coroutineScope.launch {
            val result = repository.importJson(rawJsonInput, replaceAll = replaceAll)
            result.onSuccess { count ->
                Toast.makeText(context, "Successfully imported $count notes!", Toast.LENGTH_SHORT).show()
                rawJsonInput = ""
                onImportSuccess()
            }.onFailure { e ->
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Import JSON Notes & Vocab",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Paste JSON code exported from another device or generated by an AI assistant (ChatGPT, Gemini, Claude). Or upload a .json file directly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Paste from clipboard button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = clipboard.primaryClip
                            if (clip != null && clip.itemCount > 0) {
                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                if (text.isNotBlank()) {
                                    rawJsonInput = text
                                    Toast.makeText(context, "Pasted from clipboard!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("import_paste_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paste Text", maxLines = 1)
                    }

                    // Pick File button
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("import_pick_file_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick File", maxLines = 1)
                    }

                    // Sample AI Data button
                    FilledTonalButton(
                        onClick = {
                            rawJsonInput = SampleData.sampleJsonString
                            Toast.makeText(context, "Loaded Sample AI Vocabulary JSON", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("import_sample_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sample", maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Raw JSON Text Area
                OutlinedTextField(
                    value = rawJsonInput,
                    onValueChange = { rawJsonInput = it },
                    placeholder = { Text("Paste [ { \"title\": \"...\", \"description\": \"...\" } ] here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_json_input"),
                    minLines = 6,
                    maxLines = 10,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                // Validation Status Banner
                if (validationMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isValid == true) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isValid == true) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isValid == true) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = validationMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isValid == true) Color(0xFF047857) else MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Merge vs Replace Mode
                Text(
                    text = "Import Strategy:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { replaceAll = false }
                        .padding(vertical = 2.dp)
                ) {
                    RadioButton(
                        selected = !replaceAll,
                        onClick = { replaceAll = false }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("Merge & Append (Recommended)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text("Keeps existing notes and adds new ones.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { replaceAll = true }
                        .padding(vertical = 2.dp)
                ) {
                    RadioButton(
                        selected = replaceAll,
                        onClick = { replaceAll = true }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text("Replace All Notes", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                        Text("Overwrites existing database with the imported list.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Import Button
                Button(
                    onClick = {
                        if (replaceAll) {
                            showConfirmReplaceDialog = true
                        } else {
                            executeImport()
                        }
                    },
                    enabled = isValid == true && detectedCount > 0,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_submit_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (detectedCount > 0) "Import $detectedCount Notes" else "Import Notes")
                }
            }
        }
    }

    // Confirmation dialog before replacing whole database
    if (showConfirmReplaceDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmReplaceDialog = false },
            title = { Text("Replace All Notes?") },
            text = { Text("This will clear your current notes and replace them with the $detectedCount imported notes. Are you sure?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmReplaceDialog = false
                        executeImport()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Replace All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReplaceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
