package gcp.global.jotdiary.view.components.audio

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import gcp.global.jotdiary.R

@Composable
fun coilImage(Url: String, Modifier: Modifier, Shape: Shape) {

    val painter = rememberAsyncImagePainter(
        model = Url,
        imageLoader = ImageLoader.Builder(LocalContext.current).crossfade(true).placeholder(R.drawable.ic_loading_foreground).crossfade(300).build()
    )

    Card(
        modifier = Modifier,
        shape = Shape
    ) {

        Image(
            painter = painter,
            contentDescription = "ImagePainter",
            contentScale = ContentScale.Crop
        )
    }

}

@Composable
fun coilImage(Uri: Uri?,  Modifier: Modifier, Shape: Shape) {

    Card(
        modifier = Modifier,
        shape = Shape
    ) {

        AsyncImage(
            model = Uri,
            contentDescription = "ImagePainter",
            contentScale = ContentScale.Crop,
        )
    }

}