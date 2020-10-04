package utilities

import java.math.BigDecimal
import javax.sound.midi.*


/**
 * Created by Iuliana Cosmina on 03/10/2020
 * Contains utility functions used to convert
 *  - String values to numbers
 *  - number to ByteArrays
 *  - ByteArrays to a Sequence of MidiEvents
 */

const val NOTE_ON: Int = 0x90
const val NOTE_OFF = 0x80

class DataSampleConverter {
    private fun convertStringToByteArray(dataPoint: String) : ByteArray {
        //println(dataPoint)
        return convertBigDecimalToByteArray(BigDecimal(dataPoint.trim()))
    }

    private fun convertBigDecimalToByteArray(dataPoint: BigDecimal) : ByteArray {
        val bigNumber = dataPoint.unscaledValue()

        //println("UnscaledValue: $bigNumber")
        val arr: ByteArray = bigNumber.toByteArray()
       // println("ResultedByteArray: ${arr.contentToString()}")
        return arr
    }

    fun convertStringListToByteArray(data: List<String>) : ByteArray {
        val theList =  mutableListOf<ByteArray>()
        var fullSize = 0;
        data.forEach{
            val arr =  convertStringToByteArray(it)
            theList.add(arr)
            fullSize += arr.size
        }
        var fullArr = ByteArray(0)
        theList.forEach {
            fullArr  = fullArr.plus(it)
        }
        //println("ResultedByteArray: ${fullArr.contentToString()}")
        return fullArr
    }

    fun buildSequence(byteArray: ByteArray) : Sequence {
        val sequence = Sequence(Sequence.PPQ, 24)

        // Obtain a MIDI track from the sequence
        val track = sequence.createTrack()

        // General MIDI sysex -- turn on General MIDI sound set
        val b = byteArrayOf(0xF0.toByte(), 0x7E, 0x7F, 0x09, 0x01, 0xF7.toByte())
        val sm = SysexMessage()
        sm.setMessage(b, b.size)
        var midiEvent = MidiEvent(sm, 0L)
        track.add(midiEvent)

        // set tempo (meta event)
        var mt = MetaMessage()
        val bt = byteArrayOf(0x02, 0x00.toByte(), 0x00)
        mt.setMessage(0x51, bt, 3)
        midiEvent = MidiEvent(mt, 0L)
        track.add(midiEvent)

        // set track name (meta event)
        mt = MetaMessage()
        val trackName = "midifile track"
        mt.setMessage(0x03, trackName.toByteArray(), trackName.length)
        midiEvent = MidiEvent(mt, 0L)
        track.add(midiEvent)

        // set omni on
        var mm = ShortMessage()
        mm.setMessage(0xB0, 0x7D, 0x00)
        midiEvent = MidiEvent(mm, 0L)
        track.add(midiEvent)

        // set poly on
        mm = ShortMessage()
        mm.setMessage(0xB0, 0x7F, 0x00)
        midiEvent = MidiEvent(mm, 0L)
        track.add(midiEvent)

        // set instrument to Piano
        mm = ShortMessage()
        mm.setMessage(0xC0, 0x00, 0x00)
        midiEvent = MidiEvent(mm, 0L)
        track.add(midiEvent)

        val fullSize = byteArray.size
        var fullNoOfTicks = 0L

        println("Converting $fullSize number of bytes")

        var index = 0
        var tick = 1L
        val duration = 20L
        val soundMapper = SoundMapper()
        while ( index  < fullSize ){
            try {
                //println("Converting to note ${byteArray[index]} ")
                val noteValue = soundMapper.convertByteToNote(byteArray[index])

                if (noteValue != 0) {
                    // node on
                    mm = ShortMessage()
                    mm.setMessage(NOTE_ON, noteValue, 0x60)
                    midiEvent = MidiEvent(mm, tick)
                    track.add(midiEvent)

                    tick += duration
                    //note off - middle C - 20 ticks later
                    mm = ShortMessage()
                    mm.setMessage(NOTE_OFF, noteValue, 0x40)
                    midiEvent = MidiEvent(mm, tick)
                    track.add(midiEvent)
                    fullNoOfTicks += tick + 1
                }
            } catch (ex: InvalidMidiDataException) {
                println("Refused Value " + byteArray[index].toInt())
            }
            ++index
        }

        //****  set end of track (meta event) 19 ticks later  ****
        mt = MetaMessage()
        val bet = byteArrayOf() // empty array
        mt.setMessage(0x2F, bet, 0)
        midiEvent = MidiEvent(mt, duration)
        track.add(midiEvent)
        return sequence
    }

}