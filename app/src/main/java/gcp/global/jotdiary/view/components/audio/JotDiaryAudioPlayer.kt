package gcp.global.jotdiary.view.components.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File

/**
 * JotDiaryAudioPlayer class.
 *
 * This class has two functions.
 * It can play audio from a file.
 * And play audio from a firebase url.
 */
class JotDiaryAudioPlayer() {

    private var player: MediaPlayer? = null

    /**
     * The playFile function creates a MediaPlayer instance, and uses it to play
     * a given audio file.
     *
     * @param file - File
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

    /**
     * The playFirebaseFile function creates a MediaPlayer instance, and uses it to play
     * a given audio from the internet (which in this case is a firebase url).
     *
     * @param firebaseUrl - String
     */
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

    /**
     * The stop function stops the player and releases the resources. Releasing the
     * resources is important because it allows the player to be used again.
     */
    fun stop() {
        player?.stop()
        player?.reset()
        player?.release()
        player = null
    }
}