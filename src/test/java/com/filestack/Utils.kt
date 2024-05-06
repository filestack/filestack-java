package org.filestack

import org.filestack.internal.BaseService
import org.filestack.internal.CdnService
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.Assert

@JvmOverloads
fun mockOkHttpResponse(code: Int, message: String = "Test message"): okhttp3.Response {
    val mediaType = "text/plain".toMediaTypeOrNull()

    return okhttp3.Response.Builder()
            .protocol(Protocol.HTTP_2)
            .code(code)
            .request(Request.Builder().url("http://localhost").build())
            .body(message.toResponseBody(mediaType))
            .message("Status code: $code")
            .build()
}

fun RequestBody.readUtf8(): String {
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}

fun RecordedRequest.assertThat(test: RecordedRequestTester.() -> Unit) {
    val tester = RecordedRequestTester(this, bodyParams())
    tester.test()
}

fun RecordedRequest.bodyParams(): Map<String, String> {
    return body.bodyParams()
}

fun Buffer.bodyParams(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    val buffer = clone()
    buffer.readUtf8Line()?.let { boundary ->
        val body = buffer.readUtf8()
        val multiPartParams = body.split(boundary)
        multiPartParams.forEach { param ->
            val parts = param.trim().split("\n")
            val key = parts.first().substringAfter("name=\"").substringBefore("\"")
            val value = parts.last()
            result[key] = value
        }
    }
    return result
}

class RecordedRequestTester(private val recordedRequest: RecordedRequest, private val params: Map<String, String>) {

    fun isPost() {
        methodIs("POST")
    }

    fun isPut() {
        methodIs("PUT")
    }

    fun isDelete() {
        methodIs("DELETE")
    }

    private fun methodIs(methodName: String) {
        Assert.assertEquals(methodName, recordedRequest.method)
    }

    fun pathIs(expected: String) {
        Assert.assertEquals(expected, recordedRequest.path)
    }

    fun bodyField(key: String, expectedValue: Any) {
        Assert.assertEquals(expectedValue.toString(), params[key])
    }

    fun bodyIs(text: String) {
        Assert.assertEquals(text, recordedRequest.body.clone().readUtf8())
    }

    fun header(key: String, expectedValue: Any) {
        Assert.assertEquals(recordedRequest.headers[key], expectedValue)
    }

    fun noField(key: String) {
        Assert.assertNull(params[key])
    }
}

abstract class RequestStoringDispatcher : Dispatcher() {

    var requests = mutableListOf<RecordedRequest>()

    override fun dispatch(request: RecordedRequest): MockResponse {
        requests.add(request)
        return dispatchFor(request)
    }

    abstract fun dispatchFor(request: RecordedRequest): MockResponse
}

fun RequestStoringDispatcher.assertThat(test: RequestStoringDispatcherTester.() -> Unit) {
    val tester = RequestStoringDispatcherTester(this)
    tester.test()
}

class RequestStoringDispatcherTester(private val dispatcher: RequestStoringDispatcher) {

    fun totalRequests(numOfRequests: Int) {
        Assert.assertEquals(numOfRequests, dispatcher.requests.size)
    }

    fun onlyOneRequest(path: String) {
        Assert.assertEquals(1,
                dispatcher.requests.filter { it.path == path }.count())
    }

    fun requestTo(path: String, test: RecordedRequestTester.() -> Unit) {
        val request = dispatcher.requests.find { it.path == path }
        request?.assertThat(test)
    }
}

fun fileLink(config: Config, cdnService: CdnService, baseService: BaseService, handle: String): FileLink {
    return FileLink(config, cdnService, baseService, handle)
}

