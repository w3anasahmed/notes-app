package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.model.Note
import com.example.data.sample.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val favoriteNotes: Flow<List<Note>> = noteDao.getFavoriteNotes()

    fun searchNotes(query: String): Flow<List<Note>> {
        return if (query.isBlank()) {
            noteDao.getAllNotes()
        } else {
            noteDao.searchNotes(query.trim())
        }
    }

    suspend fun insert(note: Note) {
        val cleanNote = note.copy(
            id = if (note.id.isBlank()) UUID.randomUUID().toString() else note.id,
            updatedAt = System.currentTimeMillis()
        )
        noteDao.insertNote(cleanNote)
    }

    suspend fun insertAll(notes: List<Note>) {
        val cleanNotes = notes.map {
            it.copy(
                id = if (it.id.isBlank()) UUID.randomUUID().toString() else it.id,
                updatedAt = System.currentTimeMillis()
            )
        }
        noteDao.insertNotes(cleanNotes)
    }

    suspend fun update(note: Note) {
        noteDao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteById(id: String) {
        noteDao.deleteNoteById(id)
    }

    suspend fun deleteAll() {
        noteDao.deleteAllNotes()
    }

    suspend fun toggleFavorite(note: Note) {
        noteDao.updateNote(note.copy(isFavorite = !note.isFavorite))
    }

    suspend fun populateSampleIfEmpty() {
        val current = noteDao.getAllNotes().first()
        if (current.isEmpty()) {
            noteDao.insertNotes(SampleData.sampleNotes)
        }
    }

    /**
     * Serializes a list of notes to formatted JSON.
     */
    fun exportToJson(notes: List<Note>, indentSpaces: Int = 2): String {
        val jsonArray = JSONArray()
        for (note in notes) {
            val obj = JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("description", note.description)

            val bulletsArray = JSONArray()
            note.bullets.forEach { bulletsArray.put(it) }
            obj.put("bullets", bulletsArray)

            val tagsArray = JSONArray()
            note.tags.forEach { tagsArray.put(it) }
            obj.put("tags", tagsArray)

            obj.put("sourceApp", note.sourceApp)
            obj.put("contextSnippet", note.contextSnippet)
            obj.put("createdAt", note.createdAt)
            obj.put("updatedAt", note.updatedAt)
            obj.put("isFavorite", note.isFavorite)

            jsonArray.put(obj)
        }
        return jsonArray.toString(indentSpaces)
    }

    /**
     * Parses raw JSON string into List<Note>.
     * Automatically strips AI markdown code fences (```json ... ```) if present!
     */
    fun parseJsonNotes(rawJson: String): Result<List<Note>> {
        return runCatching {
            var cleaned = rawJson.trim()
            // Strip markdown block fences like ```json ... ```
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replace(Regex("^```[a-zA-Z]*\\s*"), "")
                if (cleaned.endsWith("```")) {
                    cleaned = cleaned.substring(0, cleaned.length - 3)
                }
                cleaned = cleaned.trim()
            }

            val notesList = mutableListOf<Note>()

            if (cleaned.startsWith("[")) {
                val jsonArray = JSONArray(cleaned)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.optJSONObject(i) ?: continue
                    notesList.add(parseSingleNoteObject(obj))
                }
            } else if (cleaned.startsWith("{")) {
                // If the user pasted an object with a "notes" or "items" array, or a single note object
                val rootObj = JSONObject(cleaned)
                if (rootObj.has("notes") && rootObj.optJSONArray("notes") != null) {
                    val arr = rootObj.getJSONArray("notes")
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        notesList.add(parseSingleNoteObject(obj))
                    }
                } else if (rootObj.has("items") && rootObj.optJSONArray("items") != null) {
                    val arr = rootObj.getJSONArray("items")
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        notesList.add(parseSingleNoteObject(obj))
                    }
                } else {
                    // Single note object
                    notesList.add(parseSingleNoteObject(rootObj))
                }
            } else {
                throw IllegalArgumentException("Expected a JSON Array '[...]' or JSON Object '{...}'")
            }

            if (notesList.isEmpty()) {
                throw IllegalArgumentException("No valid notes found in provided JSON.")
            }

            notesList
        }
    }

    private fun parseSingleNoteObject(obj: JSONObject): Note {
        val id = if (obj.has("id") && !obj.isNull("id")) obj.optString("id", "") else ""
        val title = obj.optString("title", "").ifBlank {
            obj.optString("word", "").ifBlank {
                obj.optString("term", "Untitled Note")
            }
        }
        val description = obj.optString("description", "").ifBlank {
            obj.optString("meaning", "").ifBlank {
                obj.optString("definition", "")
            }
        }

        val bullets = mutableListOf<String>()
        val bulletsArray = obj.optJSONArray("bullets") ?: obj.optJSONArray("points") ?: obj.optJSONArray("takeaways")
        if (bulletsArray != null) {
            for (j in 0 until bulletsArray.length()) {
                val b = bulletsArray.optString(j, "").trim()
                if (b.isNotBlank()) bullets.add(b)
            }
        }

        val tags = mutableListOf<String>()
        val tagsArray = obj.optJSONArray("tags") ?: obj.optJSONArray("categories")
        if (tagsArray != null) {
            for (j in 0 until tagsArray.length()) {
                val t = tagsArray.optString(j, "").trim()
                if (t.isNotBlank()) tags.add(t)
            }
        }

        val sourceApp = obj.optString("sourceApp", "JSON Import")
        val contextSnippet = obj.optString("contextSnippet", "")
        val createdAt = if (obj.has("createdAt")) obj.optLong("createdAt", System.currentTimeMillis()) else System.currentTimeMillis()
        val updatedAt = if (obj.has("updatedAt")) obj.optLong("updatedAt", System.currentTimeMillis()) else createdAt
        val isFavorite = obj.optBoolean("isFavorite", false)

        return Note(
            id = id.ifBlank { UUID.randomUUID().toString() },
            title = title,
            description = description,
            bullets = bullets,
            tags = tags,
            sourceApp = sourceApp,
            contextSnippet = contextSnippet,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isFavorite = isFavorite
        )
    }

    suspend fun importJson(rawJson: String, replaceAll: Boolean = false): Result<Int> {
        val parseResult = parseJsonNotes(rawJson)
        return parseResult.mapCatching { notes ->
            if (replaceAll) {
                noteDao.deleteAllNotes()
            }
            insertAll(notes)
            notes.size
        }
    }
}
