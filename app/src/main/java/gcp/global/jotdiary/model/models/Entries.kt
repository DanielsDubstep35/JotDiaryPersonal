package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

/**
 * data class Entries
 * This model stores attributes of the Entries.
 */
data class Entries(
    val entryId: String = "",
    val entryName: String = "Diary Entry",
    val entryDescription: String = "Describe yourself :)",
    val entryMood: Int = 4,
    val entryAudioUrl: String = "",
    val entryImageUrl: String = "",
    val entryDate: Timestamp = Timestamp.now()
)
