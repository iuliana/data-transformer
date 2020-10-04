package utilities

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

/**
 * Created by Iuliana Cosmina on 03/10/2020
 */

class ByteArrayToSoundConverter {

    /**
     * Can be used to play a data sample using a Audio PCM format (basically is just noise)
     */
    fun playSound(dataSample: ByteArray) {
        val af = AudioFormat(DATA_PER_SEC, 8, 1, true, false)
        val sdl = AudioSystem.getSourceDataLine(af)
        sdl.open()
        sdl.start()
        sdl.write(dataSample, 0, dataSample.size - 1)
        sdl.drain()
        sdl.stop()
    }

}