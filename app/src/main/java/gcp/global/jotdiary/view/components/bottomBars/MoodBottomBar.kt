package gcp.global.jotdiary.view.components.bottomBars

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import gcp.global.jotdiary.R
import gcp.global.jotdiary.viewmodel.EntryViewModel

@Composable
fun MoodBottomBar(
    entryViewModel: EntryViewModel?,
    mood: Int
) {

    val unselectedEmoji = Modifier
        .alpha(0.3f).scale(0.5f)

    val selectedEmoji = Modifier
        .alpha(1f).scale(1f)

   BottomAppBar(
       backgroundColor = MaterialTheme.colors.primary,
       contentColor = MaterialTheme.colors.onSurface,
   ) {

       Row(
           modifier = Modifier.fillMaxSize(),
           horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
       ) {

           IconButton(onClick = {
               entryViewModel?.onMoodChange(1)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.saddest),
                   contentDescription = "saddest emoji",
                   modifier = if (mood == 1) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(2)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.sadder),
                   contentDescription = "sadder emoji",
                   modifier = if (mood == 2) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(3)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.sad),
                   contentDescription = "sad emoji",
                   modifier = if (mood == 3) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(4)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.ok),
                   contentDescription = "neutral emoji",
                   modifier = if (mood == 4 || mood > 7 || mood < 1) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(5)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.happy),
                   contentDescription = "happy emoji",
                   modifier = if (mood == 5) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(6)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.happier),
                   contentDescription = "happier emoji",
                   modifier = if (mood == 6) selectedEmoji else unselectedEmoji
               )
           }

           IconButton(onClick = {
               entryViewModel?.onMoodChange(7)
           }) {
               Image(
                   painter = painterResource(id = R.drawable.happiest),
                   contentDescription = "happiest emoji",
                   modifier = if (mood == 7) selectedEmoji else unselectedEmoji
               )
           }
       }
   }
}