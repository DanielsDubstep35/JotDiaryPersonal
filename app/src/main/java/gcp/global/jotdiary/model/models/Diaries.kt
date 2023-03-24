package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

data class Diaries(
    val diaryId: String = "",
    val diaryTitle: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val diaryDescription: String = "",
    val diaryCreatedDate: Timestamp = Timestamp.now()
)
