package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

/**
 * class Diary
 * This model stores attributes of a Diary.
 * Look at StorageRepository to see how it is used.
 * Look at Moment model as well.
 *
 * @see Moment
 * @see gcp.global.jotdiary.model.repository.StorageRepository
 */

data class Diary(
    val diaryId: String = "",
    val diaryTitle: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val diaryDescription: String = "",
    val diaryCreatedDate: Timestamp = Timestamp.now()
)
