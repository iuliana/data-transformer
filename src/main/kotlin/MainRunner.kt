import utilities.ByteArrayToSoundConverter
import utilities.DataSampleConverter
import utilities.FileSplitter
import utilities.PROCESSED
import java.io.File
import javax.sound.midi.MidiSystem

/**
 * Created by Iuliana Cosmina on 03/10/2020
 * Replace
 */
const val MIDS = "/Users/iulianacosmina/Work/data-transformer/src/main/resources/mid/"
fun main() {

        // Create a new MIDI sequence with 24 ticks per beat, per file
        val converter = DataSampleConverter()
        File(PROCESSED).walk().filter { !it.isDirectory }.forEach {
            val data = it.bufferedReader().readLines()
            val arr =  converter.convertStringListToByteArray(data)
            val sequence = converter.buildSequence(arr)

            val midiName = "mid" + it.name + ".mid"
            val midFile  = File(MIDS + midiName)
            MidiSystem.write(sequence,1,midFile)

            //if (true) return  /// for now we just play one file
        }

}


class DataPlayer {
    fun playData(file: File){
        val converter = DataSampleConverter()
        val player = ByteArrayToSoundConverter()
        val data = file .bufferedReader().readLines()
        val arr = converter.convertStringListToByteArray(data)
        player.playSound(arr)
    }
}