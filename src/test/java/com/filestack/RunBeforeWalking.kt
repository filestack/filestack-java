package com.filestack

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val config = Config("AgtSuOqcERh2LnIglNJAAz")
    val client = Client(config)

    val CHARS = arrayOf('a', 'b', 'c', 'd', 'e', 'f', 'h')
    fun tempFile(prefix: String = "filestack", postfix: String = ".tmp", sizeInBytes: Long = 0) =
            File.createTempFile(prefix, postfix).apply {
                val random = Random()
                val outputStream = BufferedOutputStream(FileOutputStream(this)).writer()
                outputStream.use {
                    for (i in 0 until sizeInBytes) {
                        it.write(CHARS[random.nextInt(CHARS.size)].toInt())
                    }
                }
                deleteOnExit()
            }

    val logger = Logger.getLogger("MyLogger")
    val file = tempFile(sizeInBytes = 10 * 1024 * 1024 + 4096)
    var handle = "NO_HANDLE"
    val time = measureTimeMillis {
        val fileLink = client.upload(file.inputStream(), file.length().toInt(), false)
        handle = fileLink.handle
    }

    println(time.toFloat() / 1000)
    println(handle)
}

