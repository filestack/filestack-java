package com.filestack.internal

import com.filestack.tempFile
import com.filestack.write
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor

class BaseServiceTest {

    val networkClient: NetworkClient = mock()
    val baseService = BaseService(networkClient)

    @Test
    fun overwrite() {
        val file = tempFile(postfix = ".txt").apply {
            write("file_content")
        }

        val requestBody = RequestBody.create(
                MediaType.get("text/plain"),
                file
        )

        baseService.overwrite("my_handle", "my_policy", "my_signature", requestBody)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value
        assertEquals(
                HttpUrl.get("https://www.filestackapi.com/api/file/my_handle?policy=my_policy&signature=my_signature"),
                request.url())
        assertEquals("POST", request.method())
        val bodyBuffer = Buffer()
        requestBody.writeTo(bodyBuffer)
        assertEquals("file_content", bodyBuffer.readUtf8())
    }

    @Test
    fun delete() {
        baseService.delete("my_handle", "api_key", "my_policy", "my_signature")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value
        assertEquals(
                HttpUrl.get("https://www.filestackapi.com/api/file/my_handle?key=api_key&policy=my_policy&signature=my_signature"),
                request.url())
        assertEquals("DELETE", request.method())
    }
}