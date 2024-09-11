package org.filestack.internal

import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NetworkClientTest {

    @get:Rule
    val server = MockWebServer()
    val serverUrl = server.url("/")
    val request = Request.Builder().url(serverUrl).build()
    val okHttpClient = OkHttpClient()

    val networkClient = NetworkClient(okHttpClient, Gson())

    @Test
    fun `returns successful results`() {
        server.enqueue(MockResponse())

        val response = networkClient.call(request)

        assertTrue(response.isSuccessful)

        server.enqueue(MockResponse().setBody("""{
              "text": "some_text",
              "number": 32
            }""")
        )

        val result = networkClient.call(request, Foo::class.java)

        assertTrue(result.isSuccessful)
        assertEquals(32, result.data!!.number)
        assertEquals("some_text", result.data!!.text)
    }

    @Test
    fun `returns invalid responses`() {
        server.enqueue(MockResponse().setResponseCode(403).setBody("Invalid Request"))

        val response = networkClient.call(request, Foo::class.java)

        assertFalse(response.isSuccessful)
        assertEquals(403, response.code())
    }

    @Test(expected = JsonParseException::class)
    fun `does not catch JsonParseExceptions`() {
        server.enqueue(MockResponse().setBody("-1_not_a_json"))

        networkClient.call(request, Foo::class.java)
    }

    private class Foo(val text: String, val number: Int)
}