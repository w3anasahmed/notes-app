package com.example.data.sample

import com.example.data.model.Note

object SampleData {

    val sampleNotes = listOf(
        Note(
            id = "note-sample-001",
            title = "Serendipity",
            description = "The faculty or phenomenon of finding valuable or agreeable things not sought for; a fortunate accident.",
            bullets = listOf(
                "Etymology: Coined by Horace Walpole in 1754, inspired by the fairy tale 'The Three Princes of Serendip'.",
                "Synonyms: Providence, lucky break, fluke, pleasant surprise.",
                "Example in Literature: 'It was by pure serendipity that the scientist stumbled upon penicillin in an unattended petri dish.'",
                "Memory Tip: Think of 'Serene + Dip' = dipping pleasantly into unexpected good fortune."
            ),
            tags = listOf("vocabulary", "literature", "advanced-english"),
            sourceApp = "Book Reader (Moon+)",
            contextSnippet = "She gazed through the rainy cafe window, pondering the sheer serendipity of encountering her childhood mentor in such a bustling metropolis.",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2,
            isFavorite = true
        ),
        Note(
            id = "note-sample-002",
            title = "Ephemeral",
            description = "Lasting for a very short time; transitory; fleeting.",
            bullets = listOf(
                "Greek Origin: 'ephemeros' meaning lasting only a day (epi = upon, hemera = day).",
                "Related Concepts: Ephemera (collectible paper items meant for short use, like concert tickets).",
                "Key Antonyms: Permanent, everlasting, perennial, enduring.",
                "Usage: Used often for natural beauty like cherry blossoms, morning dew, or digital stories."
            ),
            tags = listOf("philosophy", "vocabulary", "metaphor"),
            sourceApp = "PDF Reader",
            contextSnippet = "Human glory is fundamentally ephemeral, like footsteps pressed into the ocean sand before the morning tide.",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            isFavorite = false
        ),
        Note(
            id = "note-sample-003",
            title = "Ubiquitous",
            description = "Present, appearing, or found everywhere simultaneously; omnipresent.",
            bullets = listOf(
                "Origin: From Latin 'ubique' meaning 'everywhere'.",
                "Modern Context: Often used in technology (e.g., 'ubiquitous computing', smartphones).",
                "Collocations: Ubiquitous presence, ubiquitous access, ubiquitous symbols.",
                "Example: 'Smartphones have transformed from luxury gadgets into ubiquitous tools of daily survival.'"
            ),
            tags = listOf("tech", "academic", "vocabulary"),
            sourceApp = "WhatsApp",
            contextSnippet = "Check this article out: AI is becoming so ubiquitous in modern workflows that manual transcription feels archaic.",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3,
            isFavorite = true
        ),
        Note(
            id = "note-sample-004",
            title = "Quixotic",
            description = "Exceedingly idealistic; unrealistic, extravagant, and impractical in the pursuit of romantic or unattainable goals.",
            bullets = listOf(
                "Origin: Derived from Miguel de Cervantes' 1605 novel character 'Don Quixote'.",
                "Nuance: Implies chivalrous enthusiasm combined with naive blindness to harsh reality.",
                "Pronunciation: kwik-SOT-ik (/kwɪkˈsɒt.ɪk/).",
                "Synonyms: Visionary, utopian, starry-eyed, chimerical."
            ),
            tags = listOf("literature", "character-traits", "gre-prep"),
            sourceApp = "Chrome Browser",
            contextSnippet = "His quixotic quest to build a perpetual motion machine was met with affectionate laughter by his colleagues.",
            createdAt = System.currentTimeMillis() - 1000 * 60 * 45,
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 45,
            isFavorite = false
        )
    )

    val sampleJsonString: String = """
[
  {
    "id": "ai-vocab-sample-01",
    "title": "Petrichor",
    "description": "A pleasant, distinctive smell that frequently accompanies the first rain after a long period of warm, dry weather.",
    "bullets": [
      "Origin: Coined by researchers Bear & Thomas in 1964 from Greek 'petra' (stone) + 'ichor' (the ethereal fluid of gods).",
      "Scientific cause: Release of geosmin and plant oils trapped in porous rock and soil into aerosols.",
      "Sensory note: Earthy, musky, rejuvenating aroma deeply tied to human evolutionary relief.",
      "Example: 'The warm desert asphalt released an intoxicating petrichor as the monsoon began.'"
    ],
    "tags": ["nature", "sensory", "science", "vocabulary"],
    "sourceApp": "AI Generated",
    "contextSnippet": "A faint petrichor rose from the sun-baked cobblestones as the first heavy raindrops pelted the square.",
    "createdAt": 1727700000000,
    "updatedAt": 1727700000000,
    "isFavorite": true
  },
  {
    "id": "ai-vocab-sample-02",
    "title": "Mellifluous",
    "description": "Sweet or musical; pleasant to hear; flowing like honey.",
    "bullets": [
      "Etymology: Latin 'mel' (honey) + 'fluere' (to flow).",
      "Common pairings: Voice, melody, prose, tone.",
      "Opposite: Cacophonous, jarring, discordant, grating.",
      "Quick memory aid: Mel (honey like caramel) + fluid (flowing)."
    ],
    "tags": ["audio", "literature", "advanced-english"],
    "sourceApp": "AI Generated",
    "contextSnippet": "The audiobook narrator had a mellow, mellifluous baritone that kept listeners mesmerized for hours.",
    "createdAt": 1727703600000,
    "updatedAt": 1727703600000,
    "isFavorite": false
  }
]
""".trimIndent()

    val aiPromptTemplate: String = """
You are my personal vocabulary and knowledge note assistant.
Analyze the text I provide and extract important words, concepts, or quotes into a structured note list.

Output ONLY a valid JSON array conforming to this exact schema (no markdown formatting fences, just raw JSON or inside a ```json block):

[
  {
    "id": "unique-id-or-leave-blank",
    "title": "Word or Concept Name",
    "description": "Clear, comprehensive definition or primary explanation.",
    "bullets": [
      "Etymology or origin breakdown",
      "Synonyms and Antonyms",
      "Key takeaways or illustrative example sentences",
      "Memory trigger or mnemonic hook"
    ],
    "tags": ["vocabulary", "category-tag"],
    "sourceApp": "AI Assistant",
    "contextSnippet": "Original sentence where this word appeared or was referenced.",
    "isFavorite": false
  }
]

Please process the following passage:
[PASTE YOUR BOOK CHAPTER, ARTICLE, OR WHATSAPP CHAT HERE]
""".trimIndent()

    val jsonSchemaSnippet: String = """
{
  "${'$'}schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "VocabClipNotes",
  "type": "array",
  "items": {
    "type": "object",
    "required": ["title"],
    "properties": {
      "id": { "type": "string", "description": "Optional UUID; auto-generated if omitted" },
      "title": { "type": "string", "description": "Title, word, or concept name (Required)" },
      "description": { "type": "string", "description": "Detailed explanation or definition" },
      "bullets": { 
        "type": "array", 
        "items": { "type": "string" },
        "description": "List of bullet points, takeaways, synonyms, examples" 
      },
      "tags": { 
        "type": "array", 
        "items": { "type": "string" },
        "description": "Category tags, e.g. ['reading', 'gre']" 
      },
      "sourceApp": { "type": "string", "description": "Name of app where clipped (e.g. WhatsApp, Book Reader)" },
      "contextSnippet": { "type": "string", "description": "Quoted sentence from original text" },
      "createdAt": { "type": "integer", "description": "Timestamp in milliseconds" },
      "updatedAt": { "type": "integer", "description": "Last modified timestamp" },
      "isFavorite": { "type": "boolean", "description": "Bookmarked or starred" }
    }
  }
}
""".trimIndent()

    val typeScriptSnippet: String = """
export interface VocabNote {
  id?: string;
  title: string;
  description?: string;
  bullets?: string[];
  tags?: string[];
  sourceApp?: string;
  contextSnippet?: string;
  createdAt?: number;
  updatedAt?: number;
  isFavorite?: boolean;
}

export type VocabExport = VocabNote[];
""".trimIndent()

    val kotlinSnippet: String = """
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val bullets: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val sourceApp: String = "Text Selection",
    val contextSnippet: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
""".trimIndent()

    val sampleBookPassage: String = """
The old bookbinder adjusted his spectacles, admiring the exquisite parchment before him. "Language," he whispered softly, "is not merely a system of signs; it possesses a certain serendipity that binds human souls across centuries. Consider how an ephemeral phrase spoken in a fleeting conversation can echo with ubiquitous resonance across generations."

Outside the cobblestone workshop, the autumn rain began to fall in earnest, filling the quiet street with an unmistakable petrichor. A quixotic customer lingered by the doorway, debating whether to brave the storm or lose another hour among the leather-bound treasures.
""".trimIndent()
}
