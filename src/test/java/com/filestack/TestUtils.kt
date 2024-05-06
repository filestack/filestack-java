package org.filestack

import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
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
    try {
        var sink = sink();
        val bufferedSink = sink.buffer()
        bufferedSink.writeUtf8(text);
        bufferedSink.emit()
        bufferedSink.close();
    } catch (e: IOException){
        e.printStackTrace()
    }

   // return sink.readUtf8();
}
