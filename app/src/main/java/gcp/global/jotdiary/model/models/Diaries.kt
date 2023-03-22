package gcp.global.jotdiary.model.models

import com.google.firebase.Timestamp

data class Diaries(
    val diaryId: String = "",
    val diaryTitle: String = "",
    val userId: String = "",
    val imageUrl: String = "https://www.travelandleisure.com/thmb/KTIha5CLifSoUD3gx0YP51xc3rY=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/blue0517-4dfc85cb0200460ab717b101ac07888f.jpg",
    val diaryDescription: String = "",
    val diaryCreatedDate: Timestamp = Timestamp.now()
)
