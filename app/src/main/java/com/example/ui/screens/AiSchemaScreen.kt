package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.repository.NoteRepository
import com.example.data.sample.SampleData
import com.example.ui.components.JsonCodeViewer
import kotlinx.coroutines.launch

@Composable
fun AiSchemaScreen(
    repository: NoteRepository,
    onSampleImported: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var schemaTab by remember { mutableIntStateOf(0) } // 0: JSON Schema, 1: TypeScript, 2: Kotlin

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Card: Compatibility & AI Integration
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Data Schema & Structure",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Standard specification for AI note generation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "To let AI models (ChatGPT, Gemini, Claude) generate notes and vocabulary for you with full bullet points, origins, and synonyms, use the schema and prompt template below. Any valid JSON output can be pasted directly into VocabClip!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 1: Copyable AI Prompt Template
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ready-to-Use AI Prompt Template",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("AI Prompt Template", SampleData.aiPromptTemplate))
                            Toast.makeText(context, "AI Prompt Template copied! Paste it into ChatGPT or Gemini.", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("copy_ai_prompt_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Prompt")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Paste this prompt into ChatGPT, Gemini, or Claude along with a book chapter or conversation. The AI will output perfectly formatted notes that you can import into VocabClip in seconds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = SampleData.aiPromptTemplate.lines().take(7).joinToString("\n") + "\n...",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 2: Sample JSON Code & Live Test Import
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SAMPLE JSON SPECIFICATION",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = repository.importJson(SampleData.sampleJsonString, replaceAll = false)
                        result.onSuccess { count ->
                            Toast.makeText(context, "Imported $count sample AI notes into your library!", Toast.LENGTH_SHORT).show()
                            onSampleImported()
                        }.onFailure {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.testTag("test_import_ai_sample_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test Import This Sample", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        JsonCodeViewer(
            code = SampleData.sampleJsonString,
            title = "ai_compatible_sample.json",
            maxHeight = 320
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: Field-by-Field Schema Dictionary
        Text(
            text = "DATA FIELD SPECIFICATIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        SchemaFieldCard(
            fieldName = "title",
            type = "String (Required)",
            description = "The target vocabulary word, phrase, or note concept (e.g. \"Serendipity\")."
        )
        SchemaFieldCard(
            fieldName = "description",
            type = "String (Optional)",
            description = "Comprehensive definition, core explanation, or high-level meaning."
        )
        SchemaFieldCard(
            fieldName = "bullets",
            type = "Array<String> (Optional)",
            description = "Structured list of bullet points: etymology/origins, synonyms, antonyms, example sentences, memory tips, or key takeaways."
        )
        SchemaFieldCard(
            fieldName = "tags",
            type = "Array<String> (Optional)",
            description = "Category tags for organization and filtering (e.g. [\"reading\", \"gre\", \"philosophy\"])."
        )
        SchemaFieldCard(
            fieldName = "sourceApp",
            type = "String (Optional)",
            description = "Origin of note: WhatsApp, Book Reader, Kindle, PDF, Chrome, or AI Assistant."
        )
        SchemaFieldCard(
            fieldName = "contextSnippet",
            type = "String (Optional)",
            description = "Quoted sentence or book passage where the word was encountered."
        )
        SchemaFieldCard(
            fieldName = "isFavorite",
            type = "Boolean (Optional, default: false)",
            description = "Whether the note is marked as starred or prioritized."
        )

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION 4: Code Implementations (JSON Schema / TypeScript / Kotlin)
        Text(
            text = "DEVELOPER CODE STRUCTURES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = schemaTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = schemaTab == 0,
                onClick = { schemaTab = 0 },
                text = { Text("JSON Schema", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = schemaTab == 1,
                onClick = { schemaTab = 1 },
                text = { Text("TypeScript", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = schemaTab == 2,
                onClick = { schemaTab = 2 },
                text = { Text("Kotlin", style = MaterialTheme.typography.labelSmall) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val currentCode = when (schemaTab) {
            0 -> SampleData.jsonSchemaSnippet
            1 -> SampleData.typeScriptSnippet
            else -> SampleData.kotlinSnippet
        }
        val currentTitle = when (schemaTab) {
            0 -> "schema.json"
            1 -> "VocabNote.ts"
            else -> "Note.kt"
        }

        JsonCodeViewer(
            code = currentCode,
            title = currentTitle,
            maxHeight = 280
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SchemaFieldCard(
    fieldName: String,
    type: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fieldName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
