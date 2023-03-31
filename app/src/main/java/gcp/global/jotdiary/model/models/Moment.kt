package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

/**
 * class Moment
 * This model stores the attributes of a moment.
 * A Diary can have many moments.
 * Look at StorageRepository to see how it is used.
 * Look at Diary model as well.
 *
 * @see Diary
 * @see gcp.global.jotdiary.model.repository.StorageRepository
 */
data class Moment(
    val momentId: String = "",
    val momentName: String = "Diary Entry",
    val momentDescription: String = "How was your day",
    val momentMood: Int = 4,
    val momentDate: Timestamp = Timestamp.now()
)

