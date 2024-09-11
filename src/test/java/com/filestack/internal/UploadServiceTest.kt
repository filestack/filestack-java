package org.filestack.internal

import org.filestack.bodyParams
import org.filestack.internal.responses.CompleteResponse
import org.filestack.internal.responses.StartResponse
import org.filestack.internal.responses.UploadResponse
import org.filestack.tempFile
import com.nhaarman.mockitokotlin2.eq
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
import java.net.URLConnection

class UploadServiceTest {

    val networkClient: NetworkClient = mock()
    val uploadService = UploadService(networkClient)

    @Test
    fun start() {
        val file = tempFile(sizeInBytes = 1024)

        val baseParams = mutableMapOf<String, RequestBody>()
        baseParams["apikey"] = Util.createStringPart("api_key")
        baseParams["size"] = Util.createStringPart(file.length().toString())
        baseParams["policy"] = Util.createStringPart("my_policy")
        baseParams["signature"] = Util.createStringPart("my_signature")

        uploadService.start(baseParams)

        val argumentCaptor = ArgumentCaptor.forClass(okhttp3.Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(StartResponse::class.java))

        val request = argumentCaptor.value

        assertEquals(
                HttpUrl.get("https://upload.filestackapi.com/multipart/start"),
                request.url()
        )
        assertEquals("POST", request.method())

        val bodyParams = request.bodyParams()
        assertEquals("api_key", bodyParams["apikey"])
        assertEquals(file.length().toString(), bodyParams["size"])
        assertEquals("my_policy", bodyParams["policy"])
        assertEquals("my_signature", bodyParams["signature"])
    }

    @Test
    fun upload() {
        val fileToUpload = tempFile(postfix = ".jpg", sizeInBytes = 1024)
        val type = URLConnection.guessContentTypeFromName(fileToUpload.name)
        val requestBody = RequestBody.create(MediaType.get(type), fileToUpload)

        val params = mutableMapOf<String, RequestBody>()
        params["apikey"] = Util.createStringPart("api_key")
        params["size"] = Util.createStringPart(fileToUpload.length().toString())
        params["policy"] = Util.createStringPart("my_policy")
        params["signature"] = Util.createStringPart("my_signature")
        params["content"] = requestBody

        uploadService.upload(params)

        val argumentCaptor = ArgumentCaptor.forClass(okhttp3.Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(UploadResponse::class.java))

        val request = argumentCaptor.value

        assertEquals(
                HttpUrl.get("https://upload.filestackapi.com/multipart/upload"),
                request.url()
        )
        assertEquals("POST", request.method())

        val bodyParams = request.bodyParams()
        assertEquals("api_key", bodyParams["apikey"])
        assertEquals(fileToUpload.length().toString(), bodyParams["size"])
        assertEquals("my_policy", bodyParams["policy"])
        assertEquals("my_signature", bodyParams["signature"])
    }

    @Test
    fun uploadS3() {
        val headers = mapOf(
                "Header1" to "HeaderValue1",
                "Header2" to "HeaderValue2"
        )
        val url = "https://s3.url.test.com"
        val fileToUpload = tempFile(postfix = ".jpg", sizeInBytes = 1024)
        val type = URLConnection.guessContentTypeFromName(fileToUpload.name)
        val requestBody = RequestBody.create(MediaType.get(type), fileToUpload)

        uploadService.uploadS3(headers, url, requestBody)

        val argumentCaptor = ArgumentCaptor.forClass(okhttp3.Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value

        assertEquals(
                HttpUrl.get(url),
                request.url()
        )
        assertEquals("PUT", request.method())

        val requestHeaders = request.headers()
        assertEquals("HeaderValue1", requestHeaders.get("Header1"))
        assertEquals("HeaderValue2", requestHeaders.get("Header2"))

        val body = request.body()!!
        assertEquals(MediaType.get("image/jpeg"), body.contentType())
        assertEquals(fileToUpload.length(), body.contentLength())
    }

    @Test
    fun commit() {
        val file = tempFile(sizeInBytes = 1024)

        val baseParams = mutableMapOf<String, RequestBody>()
        baseParams["apikey"] = Util.createStringPart("api_key")
        baseParams["size"] = Util.createStringPart(file.length().toString())
        baseParams["policy"] = Util.createStringPart("my_policy")
        baseParams["signature"] = Util.createStringPart("my_signature")

        uploadService.commit(baseParams)

        val argumentCaptor = ArgumentCaptor.forClass(okhttp3.Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value

        assertEquals(
                HttpUrl.get("https://upload.filestackapi.com/multipart/commit"),
                request.url()
        )
        assertEquals("POST", request.method())

        val bodyParams = request.bodyParams()
        assertEquals("api_key", bodyParams["apikey"])
        assertEquals(file.length().toString(), bodyParams["size"])
        assertEquals("my_policy", bodyParams["policy"])
        assertEquals("my_signature", bodyParams["signature"])
    }

    @Test
    fun complete() {
        val file = tempFile(sizeInBytes = 1024)

        val baseParams = mutableMapOf<String, RequestBody>()
        baseParams["apikey"] = Util.createStringPart("api_key")
        baseParams["size"] = Util.createStringPart(file.length().toString())
        baseParams["policy"] = Util.createStringPart("my_policy")
        baseParams["signature"] = Util.createStringPart("my_signature")

        uploadService.complete(baseParams)

        val argumentCaptor = ArgumentCaptor.forClass(okhttp3.Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(CompleteResponse::class.java))

        val request = argumentCaptor.value

        assertEquals(
                HttpUrl.get("https://upload.filestackapi.com/multipart/complete"),
                request.url()
        )
        assertEquals("POST", request.method())

        val bodyParams = request.bodyParams()
        assertEquals("api_key", bodyParams["apikey"])
        assertEquals(file.length().toString(), bodyParams["size"])
        assertEquals("my_policy", bodyParams["policy"])
        assertEquals("my_signature", bodyParams["signature"])
    }

    private fun Request.bodyParams(): Map<String, String> {
        val buffer = Buffer()
        body()?.writeTo(buffer)
        return buffer.bodyParams()
    }
}