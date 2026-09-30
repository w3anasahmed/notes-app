package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.QuickCaptureActivity
import com.example.data.sample.SampleData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimulatorScreen(
    onTriggerCapture: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var customWord by remember { mutableStateOf("Ineffable") }
    var customSource by remember { mutableStateOf("Book Reader (Moon+)") }

    fun launchRealQuickCaptureActivity(text: String, source: String) {
        val intent = Intent(context, QuickCaptureActivity::class.java).apply {
            action = Intent.ACTION_PROCESS_TEXT
            putExtra(Intent.EXTRA_PROCESS_TEXT, text)
            putExtra("selected_text", text)
        }
        context.startActivity(intent)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // How it works banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cross-App Text Selection Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "In Android, when you highlight a word in WhatsApp, Moon+ Reader, Kindle, PDF viewer, or Chrome, Android's text selection menu pops up. VocabClip registers as a system PROCESS_TEXT action. Tap any word below to see the floating Quick Capture window in action!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: Simulated Book Reader Window
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Warm book paper tone
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Moon+ Book Reader · Chapter 4",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }

                    Text(
                        text = "Page 142 of 320",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF92400E).copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selectable book passage
                SelectionContainer {
                    Text(
                        text = SampleData.sampleBookPassage,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TAP A WORD BELOW TO TRIGGER FLOATING QUICK-SAVE:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Words from the passage
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val words = listOf("serendipity", "ephemeral", "ubiquitous", "petrichor", "quixotic", "exquisite parchment")
                    words.forEach { word ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.clickable {
                                launchRealQuickCaptureActivity(word, "Book Reader (Moon+)")
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: Test Any Custom Word or Phrase
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Test Custom App Text Selection",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Enter any word or sentence to simulate highlighting it in WhatsApp, Kindle, or a PDF reader:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customWord,
                    onValueChange = { customWord = it },
                    label = { Text("Selected Word or Phrase") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulator_custom_word_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customSource,
                    onValueChange = { customSource = it },
                    label = { Text("Simulated Source App") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulator_source_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (customWord.isNotBlank()) {
                            launchRealQuickCaptureActivity(customWord, customSource)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulator_launch_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Quick Capture Window")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
