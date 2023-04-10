package gcp.global.jotdiary.view.components.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File

class JotDiaryAudioPlayer(
    private val context: Context
) {

    private var player: MediaPlayer? = null

    /**
     * Old Code for playing audio
     */

    /*
    fun playFile(file: File) {
    MediaPlayer.create(context, file.toUri()).apply {
    Log.d("///////// >:((( ////////", "ITS YOU >:(((")
    player = this
    Log.d("///////// AHHHHHHH ////////", "MEDIA PLAYER IS $this")
    start()
    }
    }
    */

    fun playFile(file: File) {
        MediaPlayer().apply {
            Log.d("// Internal Audio Triggered //", "in audio is: ${file.absolutePath}")
            player = this
            setVolume(1.0f, 1.0f)
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    fun playFirebaseFile(firebaseUrl: String) {
        MediaPlayer().apply {
            Log.d("// Firebase Audio Triggered //", "firebase audio is: $firebaseUrl")
            player = this
            setVolume(1.0f, 1.0f)
            setDataSource(firebaseUrl)
            prepare()
            start()
        }
    }

    fun stop() {
        player?.stop()
        player?.reset()
        player?.release()
        player = null
    }
}