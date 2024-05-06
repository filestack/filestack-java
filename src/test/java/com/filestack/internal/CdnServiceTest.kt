package org.filestack.internal

import org.filestack.internal.responses.StoreResponse
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import okhttp3.HttpUrl
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor

class CdnServiceTest {

    val networkClient: NetworkClient = mock()

    val cdnService = CdnService(networkClient)


    @Test
    fun get() {
        cdnService.get("my_handle", "my_policy", "my_signature")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_handle")
            .addQueryParameter("policy", "my_policy")
            .addQueryParameter("signature", "my_signature")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("GET", request.method)
    }

    @Test
    fun transform() {
        cdnService.transform("my_tasks", "my_handle")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_tasks/my_handle")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("GET", request.method)
    }

    @Test
    fun transformDebug() {
        cdnService.transformDebug("my_tasks", "my_handle")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(JsonObject::class.java))

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("debug/my_tasks/my_handle")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("GET", request.method)
    }

    @Test
    fun transformStore() {
        cdnService.transformStore("my_tasks", "my_handle")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(StoreResponse::class.java))

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_tasks/my_handle")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("POST", request.method)
    }

    @Test
    fun transformExt() {
        cdnService.transformExt("my_key", "my_tasks", "my_handle")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_key/my_tasks/my_handle")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("GET", request.method)
    }

    @Test
    fun transformDebugExt() {
        cdnService.transformDebugExt("my_key", "my_tasks", "my_url")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(JsonObject::class.java))

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_key/debug/my_tasks/my_url")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("GET", request.method)
    }

    @Test
    fun transformStoreExt() {
        cdnService.transformStoreExt("my_key", "my_tasks", "my_url")

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(StoreResponse::class.java))

        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.filestackcontent.com")
            .addPathSegments("my_key/my_tasks/my_url")
            .build();
        assertEquals(
                url,
                request.url
        )
        assertEquals("POST", request.method)
    }
}