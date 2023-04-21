package gcp.global.jotdiary.view.components.audio

import android.Manifest
import android.app.Activity
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import java.io.File

/**
 * JotDiaryAudioRecorder class.
 *
 * This class has two functions.
 * It can start recording audio.
 * And stop recording audio.
 * It First Creates a MediaRecorder instance, and then it fills a given file with
 * the audio data. when the user stops recording, the file is saved.
 */
class JotDiaryAudioRecorder @OptIn(ExperimentalPermissionsApi::class) constructor(
    private val context: Context,
    private val micPermissionState: PermissionState
) {
    var fileSize = 0
    var recording = false

    /**
     * This code creates an instance of MediaRecorder. Only one MediaRecorder should
     * be active at a time.
     *
     * @return MediaRecorder instance
     */
    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    private var recorder: MediaRecorder? = null

    /**
     * The start function checks if the user has granted permission to use the microphone
     * and if they have, it starts recording. If they haven't, it asks for permission.
     * The max file size is set to 5 MB, the output format is set to MPEG_4, the audio
     * encoder is set to AAC, and the output file is set to the file path of the file.
     *
     * @param outputFile File
     */
    @OptIn(ExperimentalPermissionsApi::class)
    fun start(outputFile: File) {
        if (!micPermissionState.status.isGranted) {
            // ask for permission
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)

            // You could also use this version:
            // micPermissionState.launchPermissionRequest()
        } else {
            if (recorder == null) {
                createRecorder().apply {
                    recorder = this
                    recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

                    // 5 MB
                    recorder?.setMaxFileSize(5 * 1024 * 1024)
                    recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    recorder?.setOutputFile(outputFile.absolutePath)

                    recorder?.prepare()
                    recording = true
                    recorder?.start()
                }
            } else {
                Log.e("JotDiaryAudioRecorder", "Recorder is already running")
            }
        }
    }

    /**
     * The stop function stops the recording and resets the recorder.
     *
     * @param outputFile File
     */
    fun stop(outputFile: File) {

        recording = false
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null

        // get file size
        fileSize = File(outputFile.toURI()).length().toInt()
    }

    /**
     * The getAudioLevels function gets the audio levels of the recording. This number
     * is used to display how loud or quiet the user is speaking.
     *
     * @return Int
     */
    fun getAudioLevels(): Int {

        // remember mutable
        var blow_value = 0

        val minSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)

        val ar = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize
        )

        val buffer = ShortArray(minSize)

        ar.startRecording()
        while (recorder != null) {
            ar.read(buffer, 0, minSize)
            for (s in buffer) {
                if (Math.abs(s.toInt()) > 0)
                {
                    blow_value = Math.abs(s.toInt()/100)
                    ar.stop()
                    return blow_value
                }
            }
        }
        return blow_value
    }

}
