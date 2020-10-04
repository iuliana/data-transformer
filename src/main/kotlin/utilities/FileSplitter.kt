package utilities

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.net.URL
import java.util.*


/**
 * Created by Iuliana Cosmina on 03/10/2020
 * Utility to split big sample files.
 */
const val DATA_PER_SEC = 16384f;
const val PROCESSED = "/Users/iulianacosmina/Work/data-transformer/src/main/resources/processed/"

/**
 * Run this to split big files into smaller ones,
 */
fun main() {
    // split big file into data for each second
    val fs = FileSplitter()
    val resourceUrl = fs.loadResource("H-H1_GWOSC_16KHZ_R2-1242442952-32.txt")
    fs.splitFile(resourceUrl)
}

class FileSplitter {
    fun loadResource(resource: String): URL =
        try {
            object {}.javaClass.getResource(resource)
        } catch (all: Exception) {
            throw RuntimeException("Failed to load resource=$resource!", all)
        }

    fun splitFile(url: URL) {
        var itemsCount = 0
        var fileCount = 1
        var listPerSecond = ArrayList<String>()
        File(url.path).forEachLine {
            if(it.isNotEmpty() && !it.startsWith("#")) {
                if (listPerSecond.size == DATA_PER_SEC.toInt()) {
                    // write file
                    val bw = BufferedWriter(FileWriter(File(PROCESSED + "gravitational_wave_strain_$fileCount.txt"), true))
                    listPerSecond.forEach{ item -> bw.write(item + "\n") }
                    bw.flush()
                    ++ fileCount
                    println("Wrote file!");
                    // re-init list
                    listPerSecond = ArrayList<String>()
                    itemsCount = 0
                } else {
                    listPerSecond.add(it)
                    ++itemsCount
                }
            }
        }
    }
}