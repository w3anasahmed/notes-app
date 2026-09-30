package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Note

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorDialog(
    initialNote: Note? = null,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    val isEdit = initialNote != null
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var description by remember { mutableStateOf(initialNote?.description ?: "") }
    var contextSnippet by remember { mutableStateOf(initialNote?.contextSnippet ?: "") }
    var sourceApp by remember { mutableStateOf(initialNote?.sourceApp ?: "Manual Entry") }

    val bullets = remember {
        mutableStateListOf<String>().apply {
            initialNote?.bullets?.let { addAll(it) }
        }
    }
    var newBulletText by remember { mutableStateOf("") }

    val tags = remember {
        mutableStateListOf<String>().apply {
            initialNote?.tags?.let { addAll(it) }
        }
    }
    var newTagText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 24.dp),
        title = {
            Text(
                text = if (isEdit) "Edit Note & Vocabulary" else "New Note & Vocabulary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(vertical = 4.dp)
            ) {
                // Word / Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Word / Title *") },
                    placeholder = { Text("e.g. Serendipity, Ephemeral, Concept...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description / Definition
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Meaning / Description") },
                    placeholder = { Text("Comprehensive definition, explanation, or concept notes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_description_input"),
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bullet Points Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListBulleted,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bullet Points & Takeaways (${bullets.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Existing Bullets
                bullets.forEachIndexed { index, bullet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { bullets.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove bullet",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Add Bullet Input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newBulletText,
                        onValueChange = { newBulletText = it },
                        placeholder = { Text("Add takeaway, example, or synonym...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("editor_new_bullet_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newBulletText.isNotBlank()) {
                                bullets.add(newBulletText.trim())
                                newBulletText = ""
                            }
                        },
                        enabled = newBulletText.isNotBlank(),
                        modifier = Modifier.testTag("editor_add_bullet_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }

                // Quick suggestions for bullets
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Synonyms: ", "Etymology: ", "Example: ", "Memory Hook: ").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                newBulletText = preset
                            }
                        ) {
                            Text(
                                text = "+ $preset",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tags (${tags.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Existing Tags
                if (tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { tags.remove(tag) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Add Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTagText,
                        onValueChange = { newTagText = it.replace(" ", "-").lowercase() },
                        placeholder = { Text("Add tag (e.g. reading, gre, novel)...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("editor_new_tag_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cleanTag = newTagText.trim().removePrefix("#")
                            if (cleanTag.isNotBlank() && !tags.contains(cleanTag)) {
                                tags.add(cleanTag)
                                newTagText = ""
                            }
                        },
                        enabled = newTagText.isNotBlank(),
                        modifier = Modifier.testTag("editor_add_tag_btn")
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Context Quote Snippet (from reading the book)
                OutlinedTextField(
                    value = contextSnippet,
                    onValueChange = { contextSnippet = it },
                    label = { Text("Original Book Quote / Context") },
                    placeholder = { Text("Quote or sentence where you read this word...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_context_input"),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Source App / Book
                OutlinedTextField(
                    value = sourceApp,
                    onValueChange = { sourceApp = it },
                    label = { Text("Source App or Book Title") },
                    placeholder = { Text("e.g. Moon+ Reader, WhatsApp, The Great Gatsby...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_source_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val noteToSave = Note(
                            id = initialNote?.id ?: java.util.UUID.randomUUID().toString(),
                            title = title.trim(),
                            description = description.trim(),
                            bullets = bullets.toList(),
                            tags = tags.toList(),
                            sourceApp = sourceApp.trim().ifBlank { "Manual Entry" },
                            contextSnippet = contextSnippet.trim(),
                            createdAt = initialNote?.createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis(),
                            isFavorite = initialNote?.isFavorite ?: false
                        )
                        onSave(noteToSave)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("editor_save_btn")
            ) {
                Text(if (isEdit) "Update Note" else "Save Note")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("editor_cancel_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
