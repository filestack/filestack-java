package org.filestack.internal

import org.filestack.tempFile
import org.filestack.write
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
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


        val requestBody = file.asRequestBody("text/plain".toMediaType())

        baseService.overwrite("my_handle", "my_policy", "my_signature", requestBody)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("www.filestackapi.com")
            .addPathSegments("api/file/my_handle")
            .addQueryParameter("policy", "my_policy")
            .addQueryParameter("signature", "my_signature")
            .build();

        val request = argumentCaptor.value
        assertEquals(
            url,
                request.url)
        assertEquals("POST", request.method)
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
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("www.filestackapi.com")
            .addPathSegments("api/file/my_handle")
            .addQueryParameter("key", "api_key")
            .addQueryParameter("policy", "my_policy")
            .addQueryParameter("signature", "my_signature")
            .build()
        assertEquals(
            url,
                request.url)
        assertEquals("DELETE", request.method)
    }
}