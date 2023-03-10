package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

/**
 @param

 */
data class Entries(
    val userId: String = "",
    val entryID: String = "",
    val diaryId: String = "",
    val diaryTitle: String = "",
    val entryName: String = "",
    val entryDescription: String = "",
    val entryMood: Int = 0,
    val entryDate: String = ""
)
