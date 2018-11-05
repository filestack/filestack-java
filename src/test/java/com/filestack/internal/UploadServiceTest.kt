package com.filestack.internal

import com.filestack.StorageOptions
import com.filestack.assertThat
import com.filestack.bodyParams
import com.filestack.internal.request.StartUploadRequest
import com.filestack.internal.request.UploadRequest
import com.filestack.tempFile
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import okhttp3.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import java.net.URLConnection

class UploadServiceTest {

    @get:Rule
    val server = MockWebServer()

    val networkClient: NetworkClient = NetworkClient(OkHttpClient(), Gson())
    val uploadService = UploadService(networkClient, server.url("/"))

    @Test
    fun `start without intelligent ingestion`() {
        val file = tempFile(sizeInBytes = 1024)
        server.enqueue(MockResponse())

        val storageOptions = StorageOptions.Builder()
                .access("my_access")
                .container("my_container")
                .filename("my_filename")
                .location( "my_location")
                .mimeType("my_mimetype")
                .region("my_region")
                .path("my_path")
                .build()

        val startUploadRequest = StartUploadRequest(
                "api_key",
                file.length(),
                false,
                "my_policy",
                "my_signature",
                storageOptions
        )

        server.enqueue(MockResponse())
        uploadService.start(startUploadRequest)

        val request = server.takeRequest()

        request.assertThat {
            isPost()
            pathIs("/multipart/start")
            noField("multipart")
            bodyField("apikey", "api_key")
            bodyField("size", file.length().toString())
            bodyField("policy", "my_policy")
            bodyField("signature", "my_signature")
            bodyField("filename", "my_filename")
            bodyField("mimetype", "my_mimetype")
            bodyField("store_location", "my_location")
            bodyField("store_region", "my_region")
            bodyField("store_container", "my_container")
            bodyField("store_path", "my_path")
            bodyField("store_access", "my_access")
        }
    }

    @Test
    fun `start with intelligent ingestion`() {
        val file = tempFile(sizeInBytes = 1024)
        server.enqueue(MockResponse())

        val storageOptions = StorageOptions.Builder()
                .access("my_access")
                .container("my_container")
                .filename("my_filename")
                .location( "my_location")
                .mimeType("my_mimetype")
                .region("my_region")
                .build()

        val startUploadRequest = StartUploadRequest(
                "api_key",
                file.length(),
                true,
                "my_policy",
                "my_signature",
                storageOptions
        )

        uploadService.start(startUploadRequest)

        val request = server.takeRequest()


        request.assertThat {
            isPost()
            pathIs("/multipart/start")
            bodyField("multipart", "true")
            bodyField("apikey", "api_key")
            bodyField("size", file.length().toString())
            bodyField("policy", "my_policy")
            bodyField("signature", "my_signature")
        }
    }

    @Test
    fun upload() {
        server.enqueue(MockResponse())
        val fileToUpload = tempFile(postfix = ".jpg", sizeInBytes = 1024)

        val uploadRequest = UploadRequest(
                "api_key",
                1,
                fileToUpload.length(),
                "my_md5",
                "response_uri",
                "response_region",
                "response_upload_id",
                true,
                512
        )

        uploadService.upload(uploadRequest)

        val request = server.takeRequest()
        request.assertThat {
            pathIs("/multipart/upload")
            isPost()
            bodyField("apikey", "api_key")
            bodyField("part", 1)
            bodyField("size", fileToUpload.length())
            bodyField("md5", "my_md5")
            bodyField("uri", "response_uri")
            bodyField("region", "response_region")
            bodyField("upload_id", "response_upload_id")
            bodyField("multipart", "true")
            bodyField("offset", 512)
        }
    }

    @Test
    fun uploadS3() {
        val networkClient: NetworkClient = mock()
        val uploadService = UploadService(networkClient)

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
        server.enqueue(MockResponse())
        val file = tempFile(sizeInBytes = 1024)

        val baseParams = mutableMapOf<String, RequestBody>()
        baseParams["apikey"] = Util.createStringPart("api_key")
        baseParams["size"] = Util.createStringPart(file.length().toString())
        baseParams["policy"] = Util.createStringPart("my_policy")
        baseParams["signature"] = Util.createStringPart("my_signature")

        uploadService.commit(baseParams)

        val request = server.takeRequest()

        request.assertThat {
            pathIs("/multipart/commit")
            isPost()
        }

        val bodyParams = request.bodyParams()
        assertEquals("api_key", bodyParams["apikey"])
        assertEquals(file.length().toString(), bodyParams["size"])
        assertEquals("my_policy", bodyParams["policy"])
        assertEquals("my_signature", bodyParams["signature"])
    }

    @Test
    fun complete() {
        server.enqueue(MockResponse())
        val file = tempFile(sizeInBytes = 1024)

        val baseParams = mutableMapOf<String, RequestBody>()
        baseParams["apikey"] = Util.createStringPart("api_key")
        baseParams["size"] = Util.createStringPart(file.length().toString())
        baseParams["policy"] = Util.createStringPart("my_policy")
        baseParams["signature"] = Util.createStringPart("my_signature")

        uploadService.complete(baseParams)

        val request = server.takeRequest()

        request.assertThat {
            pathIs("/multipart/complete")
            isPost()
        }
    }

    private fun Request.bodyParams(): Map<String, String> {
        val buffer = Buffer()
        body()?.writeTo(buffer)
        return buffer.bodyParams()
    }
}