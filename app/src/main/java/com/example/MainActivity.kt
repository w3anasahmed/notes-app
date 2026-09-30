package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Note
import com.example.data.sample.SampleData
import com.example.ui.screens.AiSchemaScreen
import com.example.ui.screens.ExportImportScreen
import com.example.ui.screens.NotesListScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as VocabClipApp
        val repository = app.repository

        setContent {
            MyApplicationTheme {
                MainAppContent(
                    repository = repository,
                    onShowToast = { msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    repository: com.example.data.repository.NoteRepository,
    onShowToast: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val notes by repository.allNotes.collectAsStateWithLifecycle(initialValue = emptyList())

    var currentTab by remember { mutableIntStateOf(0) } // 0: Notes, 1: Sync, 2: AI Schema, 3: Simulator
    var searchQuery by remember { mutableStateOf("") }

    // Back button returns to Notes tab before exiting
    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bookmarks,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "VocabClip",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Quick Load Sample Pack button in top bar
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.insertAll(SampleData.sampleNotes)
                                onShowToast("Loaded rich vocabulary sample pack!")
                            }
                        },
                        modifier = Modifier.testTag("top_sample_pack_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Load Sample Pack",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(Icons.Default.MenuBook, contentDescription = "Notes")
                    },
                    label = { Text("Notes (${notes.size})") },
                    modifier = Modifier.testTag("nav_notes")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(Icons.Default.Sync, contentDescription = "Sync & JSON")
                    },
                    label = { Text("Sync / JSON") },
                    modifier = Modifier.testTag("nav_sync")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(Icons.Default.Psychology, contentDescription = "AI & Schema")
                    },
                    label = { Text("AI Schema") },
                    modifier = Modifier.testTag("nav_ai_schema")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = {
                        Icon(Icons.Default.SmartToy, contentDescription = "Simulator")
                    },
                    label = { Text("Simulator") },
                    modifier = Modifier.testTag("nav_simulator")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> NotesListScreen(
                    notes = notes,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSaveNote = { note ->
                        coroutineScope.launch {
                            repository.insert(note)
                            onShowToast("Saved \"${note.title}\"")
                        }
                    },
                    onDeleteNote = { note ->
                        coroutineScope.launch {
                            repository.delete(note)
                            onShowToast("Deleted \"${note.title}\"")
                        }
                    },
                    onToggleFavorite = { note ->
                        coroutineScope.launch {
                            repository.toggleFavorite(note)
                        }
                    },
                    onOpenSimulator = { currentTab = 3 },
                    onLoadSamplePack = {
                        coroutineScope.launch {
                            repository.insertAll(SampleData.sampleNotes)
                            onShowToast("Loaded rich vocabulary sample pack!")
                        }
                    }
                )

                1 -> ExportImportScreen(
                    notes = notes,
                    repository = repository,
                    onRefresh = {
                        onShowToast("Database refreshed!")
                    }
                )

                2 -> AiSchemaScreen(
                    repository = repository,
                    onSampleImported = {
                        currentTab = 0
                    }
                )

                3 -> SimulatorScreen(
                    onTriggerCapture = { _, _ -> }
                )
            }
        }
    }
}
