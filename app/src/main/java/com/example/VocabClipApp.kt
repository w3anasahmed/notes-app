package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VocabClipApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }

    override fun onCreate() {
        super.onCreate()
        // Populate rich default notes on first run so the app is instantly useful
        applicationScope.launch {
            repository.populateSampleIfEmpty()
        }
    }
}
