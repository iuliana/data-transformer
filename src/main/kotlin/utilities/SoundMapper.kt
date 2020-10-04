package utilities

/**
 * Created by Iuliana Cosmina on 04/10/2020
 */
class SoundMapper {

    /**
     * Convert byte values into positive Integer values that can be played as a piano key
     */
    fun convertByteToNote(byte: Byte): Int{
        val intVal = byte.toInt()
        return if(intVal % 2 == 1) {
            (intVal + 127)/2
        } else {
            (intVal + 128)/2
        }
    }

}