package com.filestack

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*
import java.util.logging.Logger



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
    val file = tempFile(sizeInBytes = 30 * 1024 * 1024 + 4096)
    val (time, handle) = measureTime {
        val fileLink = client.upload(file.inputStream(), file.length().toInt(), false)
        fileLink.handle
    }


    logger.info("${time.toFloat() / 1000}")
    logger.info(handle)

    val fileLink = client.fileLink(handle)

    val (downloadTime, downloadedFile) = measureTime {
        fileLink.download(file.parentFile.path)
    }

    val uploadedFileBytes = Files.readAllBytes(file.toPath())
    val downloadedFileBytes = Files.readAllBytes(downloadedFile.toPath())

    val equals = Arrays.equals(uploadedFileBytes, downloadedFileBytes)
    logger.info("files are equals = $equals")
}

/**
 * Executes the given [block] and returns elapsed time in milliseconds.
 */
inline fun <T> measureTime(block: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val result = block()
    return Pair(System.currentTimeMillis() - start, result)
}

