package org.filestack

import okio.Okio
import java.io.File
import java.io.RandomAccessFile

fun tempFile(prefix: String = "filestack", postfix: String = ".tmp", sizeInBytes: Long = 0) =
        File.createTempFile(prefix, postfix).apply {
            deleteOnExit()
            if (sizeInBytes > 0) {
                val raf = RandomAccessFile(this, "rw")
                raf.setLength(sizeInBytes)
            }
        }

fun File.write(text: String) {
    Okio.buffer(Okio.sink(this)).writeUtf8(text).close()
}
