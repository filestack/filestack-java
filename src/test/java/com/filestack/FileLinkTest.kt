package org.filestack

import org.filestack.internal.NetworkClient
import org.filestack.internal.TestServiceFactory
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matcher
import org.hamcrest.beans.HasPropertyWithValue.hasProperty
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

private const val API_KEY = "iowjr230942nn2"
private const val POLICY = "policy"
private const val SIGNATURE = "signature"

class FileLinkTest {

    @get:Rule
    val server = MockWebServer()

    val config = Config(API_KEY, POLICY, SIGNATURE)

    val okHttpClient = OkHttpClient.Builder().build()
    val gson = Gson()

    val networkClient = NetworkClient(okHttpClient, gson)

    val cdnService = TestServiceFactory.cdnService(networkClient, server.url("/"))
    val baseService = TestServiceFactory.baseService(networkClient, server.url("/"))


    @Test
    fun `test overwrite`() {
        val file = tempFile(postfix = ".txt").apply { write("some text") }
        server.enqueue(MockResponse())

        val fileLink = FileLink(config, cdnService, baseService, "handle")
        fileLink.overwrite(file.path)

        server.takeRequest().assertThat {
            isPost()
            pathIs("/handle?policy=$POLICY&signature=$SIGNATURE")
            bodyIs("some text")
        }
    }

    @Test
    fun `test overwrite - throws on http errors`() {
        val file = tempFile(postfix = ".txt").apply { write("some text") }
        server.enqueue(MockResponse().setResponseCode(403).setBody("Invalid Request"))
        val fileLink = FileLink(config, cdnService, baseService, "handle")

        val exception = Assert.assertThrows(HttpException::class.java) {
            fileLink.overwrite(file.path)
        }

        Assert.assertEquals(exception.message, "Invalid Request")
        Assert.assertEquals(exception.code, 403)
    }

    @Test
    fun `test delete`() {
        server.enqueue(MockResponse())

        val fileLink = FileLink(config, cdnService, baseService, "handle")
        fileLink.delete()

        server.takeRequest().assertThat {
            isDelete()
            pathIs("/handle?key=$API_KEY&policy=$POLICY&signature=$SIGNATURE")
        }
    }

    @Test
    fun `test delete - throws on http errors`() {
        server.enqueue(MockResponse().setResponseCode(403).setBody("Invalid Request"))

        val fileLink = FileLink(config, cdnService, baseService, "handle")

        val exception = Assert.assertThrows(HttpException::class.java) {
            fileLink.delete()
        }

        Assert.assertEquals(exception.message, "Invalid Request")
        Assert.assertEquals(exception.code, 403)

    }

    private fun hasCode(code: Int): Matcher<Exception> {
        return hasProperty("code", equalTo(code))
    }


}
