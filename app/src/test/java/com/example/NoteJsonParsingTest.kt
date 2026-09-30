package com.example

import com.example.data.local.NoteDao
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import com.example.data.sample.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NoteJsonParsingTest {

    // Dummy DAO for testing serialization/deserialization logic
    private val fakeDao = object : NoteDao {
        override fun getAllNotes(): Flow<List<Note>> = flowOf(emptyList())
        override fun searchNotes(query: String): Flow<List<Note>> = flowOf(emptyList())
        override fun getFavoriteNotes(): Flow<List<Note>> = flowOf(emptyList())
        override fun getNoteById(id: String): Flow<Note?> = flowOf(null)
        override suspend fun insertNote(note: Note) {}
        override suspend fun insertNotes(notes: List<Note>) {}
        override suspend fun updateNote(note: Note) {}
        override suspend fun deleteNote(note: Note) {}
        override suspend fun deleteNoteById(id: String) {}
        override suspend fun deleteAllNotes() {}
        override fun getNoteCount(): Flow<Int> = flowOf(0)
    }

    private val repository = NoteRepository(fakeDao)

    @Test
    fun testParseSampleJsonNotes() {
        val result = repository.parseJsonNotes(SampleData.sampleJsonString)
        assertTrue("JSON parsing failed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val notes = result.getOrNull()
        assertNotNull(notes)
        assertEquals(2, notes!!.size)

        val first = notes[0]
        assertEquals("Petrichor", first.title)
        assertTrue(first.bullets.isNotEmpty())
        assertEquals(4, first.bullets.size)
        assertTrue(first.tags.contains("nature"))
        assertTrue(first.isFavorite)
    }

    @Test
    fun testParseMarkdownFencedJson() {
        val markdownJson = """
            ```json
            [
              {
                "title": "Ephemeral",
                "description": "Lasting a very short time",
                "bullets": ["Fleeting", "Transient"],
                "tags": ["vocab"]
              }
            ]
            ```
        """.trimIndent()

        val result = repository.parseJsonNotes(markdownJson)
        assertTrue("Fenced JSON parsing failed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        val notes = result.getOrNull()!!
        assertEquals(1, notes.size)
        assertEquals("Ephemeral", notes[0].title)
        assertEquals(2, notes[0].bullets.size)
    }

    @Test
    fun testExportAndReimportRoundTrip() {
        val originalNotes = SampleData.sampleNotes
        val exportedJson = repository.exportToJson(originalNotes)
        val reimportedResult = repository.parseJsonNotes(exportedJson)

        assertTrue("Reimport failed: ${reimportedResult.exceptionOrNull()?.message}", reimportedResult.isSuccess)
        val reimported = reimportedResult.getOrNull()!!
        assertEquals(originalNotes.size, reimported.size)
        assertEquals(originalNotes[0].title, reimported[0].title)
        assertEquals(originalNotes[0].bullets.size, reimported[0].bullets.size)
    }
}
