package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

/**
 * class Daries
 * This model stores attributes of the Diaries.
 */

data class Diaries(
    val diaryId: String = "",
    val diaryTitle: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val diaryDescription: String = "",
    val diaryCreatedDate: Timestamp = Timestamp.now()
)
