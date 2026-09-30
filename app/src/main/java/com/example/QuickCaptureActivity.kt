package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.example.data.model.Note
import com.example.ui.quickcapture.QuickCaptureDialogContent
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class QuickCaptureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val capturedText = extractTextFromIntent(intent)
        val sourceApp = resolveSourceAppName()

        if (capturedText.isBlank()) {
            Toast.makeText(this, "No text selected to save.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val app = application as VocabClipApp
        val repository = app.repository

        setContent {
            MyApplicationTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .systemBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    QuickCaptureDialogContent(
                        initialSelectedText = capturedText,
                        sourceAppName = sourceApp,
                        onSave = { note: Note, openFullApp: Boolean ->
                            lifecycleScope.launch {
                                repository.insert(note)
                                Toast.makeText(
                                    this@QuickCaptureActivity,
                                    "Saved \"${note.title}\" to VocabClip!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (openFullApp) {
                                    val mainIntent = Intent(this@QuickCaptureActivity, MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    }
                                    startActivity(mainIntent)
                                }
                                finish()
                            }
                        },
                        onDismiss = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun extractTextFromIntent(intent: Intent?): String {
        if (intent == null) return ""
        return when (intent.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                val charSeq = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
                charSeq?.toString() ?: ""
            }
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
            }
            else -> {
                intent.getStringExtra("selected_text") ?: ""
            }
        }
    }

    private fun resolveSourceAppName(): String {
        val caller = callingPackage ?: referrer?.authority ?: ""
        return when {
            caller.contains("whatsapp", ignoreCase = true) -> "WhatsApp"
            caller.contains("chrome", ignoreCase = true) -> "Chrome"
            caller.contains("reader", ignoreCase = true) -> "Book Reader"
            caller.contains("pdf", ignoreCase = true) -> "PDF Reader"
            caller.contains("kindle", ignoreCase = true) -> "Kindle"
            caller.contains("books", ignoreCase = true) -> "Play Books"
            caller.isNotBlank() -> caller.substringAfterLast('.').replaceFirstChar { it.uppercase() }
            else -> "Book / App Selection"
        }
    }
}
