package com.example.ui.quickcapture

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Note
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickCaptureDialogContent(
    initialSelectedText: String,
    sourceAppName: String,
    onSave: (Note, Boolean) -> Unit, // note, openFullApp
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parse the selected text:
    // If text is short (1-3 words), use as Title.
    // If longer passage, use first 3-5 words as Title, full text as context snippet / description!
    val words = remember(initialSelectedText) {
        initialSelectedText.trim().split(Regex("\\s+"))
    }
    val defaultTitle = remember(initialSelectedText) {
        if (words.size <= 4) initialSelectedText.trim() else words.take(4).joinToString(" ") + "..."
    }
    val defaultContext = remember(initialSelectedText) {
        if (words.size > 4) initialSelectedText.trim() else ""
    }

    var title by remember { mutableStateOf(defaultTitle) }
    var description by remember { mutableStateOf("") }
    var contextSnippet by remember { mutableStateOf(defaultContext) }

    val bullets = remember { mutableStateListOf<String>() }
    var newBullet by remember { mutableStateOf("") }

    val tags = remember {
        mutableStateListOf<String>().apply {
            add("quick-clip")
            if (sourceAppName.isNotBlank() && sourceAppName != "External App") {
                add(sourceAppName.lowercase().replace(" ", "-"))
            }
        }
    }
    var newTag by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .testTag("quick_capture_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdded,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Save to VocabClip",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "From: $sourceAppName",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("quick_capture_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Word / Title TextField
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Word / Phrase / Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_capture_title"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Meaning / Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Meaning or Notes (Optional)") },
                    placeholder = { Text("Enter definition, translation, or insights...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_capture_desc"),
                    minLines = 2,
                    maxLines = 4
                )

                // Context Quote Snippet
                if (contextSnippet.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = contextSnippet,
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bullet Points list
                Text(
                    text = "Bullet Points & Takeaways",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                bullets.forEachIndexed { idx, bullet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(bullet, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { bullets.removeAt(idx) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Inline Add Bullet Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newBullet,
                        onValueChange = { newBullet = it },
                        placeholder = { Text("Add bullet (e.g. synonym, example)...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_capture_bullet_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newBullet.isNotBlank()) {
                                bullets.add(newBullet.trim())
                                newBullet = ""
                            }
                        },
                        enabled = newBullet.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tags chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("#$tag", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp).clickable { tags.remove(tag) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions: Save & Return vs Save & Open App
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                val note = Note(
                                    title = title.trim(),
                                    description = description.trim(),
                                    bullets = bullets.toList(),
                                    tags = tags.toList(),
                                    sourceApp = sourceAppName,
                                    contextSnippet = contextSnippet.trim()
                                )
                                onSave(note, true)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_capture_open_app_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save & Open", maxLines = 1)
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val note = Note(
                                    title = title.trim(),
                                    description = description.trim(),
                                    bullets = bullets.toList(),
                                    tags = tags.toList(),
                                    sourceApp = sourceAppName,
                                    contextSnippet = contextSnippet.trim()
                                )
                                onSave(note, false)
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("quick_capture_save_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Save")
                    }
                }
            }
        }
    }
}
