package org.filestack.internal

import org.filestack.AppInfo
import org.filestack.readUtf8
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import okhttp3.HttpUrl
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentCaptor

class CloudServiceTest {

    val gson = Gson()
    val networkClient: NetworkClient = mock()
    val cloudService = CloudService(networkClient, gson)

    @Test
    fun prefetch() {
        val json = JsonObject()
        json.addProperty("test_value", 1)
        json.addProperty("test_value2", "value")

        cloudService.prefetch(json)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(AppInfo::class.java))
        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cloud.filestackapi.com")
            .addPathSegments("prefetch").build()

        assertEquals(
                url,
                request.url
        )

        assertEquals("POST", request.method)
        val body = request.body!!
        val utf8 = body.readUtf8()
        assertEquals("{\"test_value\":1,\"test_value2\":\"value\"}", utf8)
    }

    @Test
    fun list() {
        val json = JsonObject()
        json.addProperty("test_value", 1)
        json.addProperty("test_value2", "value")

        cloudService.list(json)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(JsonObject::class.java))
        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cloud.filestackapi.com")
            .addPathSegments("folder/list").build()

        assertEquals(
                url,
                request.url
        )

        assertEquals("POST", request.method)
        val body = request.body!!
        val utf8 = body.readUtf8()
        assertEquals("{\"test_value\":1,\"test_value2\":\"value\"}", utf8)
    }

    @Test
    fun store() {
        val json = JsonObject()
        json.addProperty("test_value", 1)
        json.addProperty("test_value2", "value")

        cloudService.store(json)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture(), eq(JsonObject::class.java))
        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cloud.filestackapi.com")
            .addPathSegments("store/").build()

        assertEquals(
                url,
                request.url
        )

        assertEquals("POST", request.method)
        val body = request.body!!
        val utf8 = body.readUtf8()
        assertEquals("{\"test_value\":1,\"test_value2\":\"value\"}", utf8)
    }

    @Test
    fun logout() {
        val json = JsonObject()
        json.addProperty("test_value", 1)
        json.addProperty("test_value2", "value")

        cloudService.logout(json)

        val argumentCaptor = ArgumentCaptor.forClass(Request::class.java)
        verify(networkClient).call(argumentCaptor.capture())
        val request = argumentCaptor.value
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("cloud.filestackapi.com")
            .addPathSegments("auth/logout").build()

        assertEquals(
                url,
                request.url
        )

        assertEquals("POST", request.method)
        val body = request.body!!
        val utf8 = body.readUtf8()
        assertEquals("{\"test_value\":1,\"test_value2\":\"value\"}", utf8)
    }
}