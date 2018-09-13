package com.filestack

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.*
import org.mockito.Mock

fun RecordedRequest.assertThat(test: RecordedRequestTester.() -> Unit) {
    val tester = RecordedRequestTester(this, bodyParams())
    tester.test()
}

fun RecordedRequest.bodyParams(): Map<String, String> {
    body.clone().let {
        val boundary = it.readUtf8Line()!!
        val body = it.readUtf8()
        val multiPartParams = body.split(boundary)
        val result = mutableMapOf<String, String>()
        multiPartParams.forEach { param ->
            val parts = param.trim().split("\n")
            val key = parts.first().substringAfter("name=\"").substringBefore("\"")
            val value = parts.last()
            result[key] = value
        }
        return result
    }

}

class RecordedRequestTester(private val recordedRequest: RecordedRequest, private val params: Map<String, String>) {

    fun isPost() {
        methodIs("POST")
    }

    fun isPut() {
        methodIs("PUT")
    }

    private fun methodIs(methodName: String) {
        assertEquals(methodName, recordedRequest.method)
    }

    fun pathIs(expected: String) {
        assertEquals(expected, recordedRequest.path)
    }

    fun bodyField(key: String, expectedValue: Any) {
        assertEquals(expectedValue.toString(), params[key])
    }

    fun header(key: String, expectedValue: Any) {
        assertEquals(recordedRequest.headers[key], expectedValue)
    }

    fun noField(key: String) {
        assertNull(params[key])
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
        assertEquals(numOfRequests, dispatcher.requests.size)
    }

    fun onlyOneRequest(path: String) {
        assertEquals(1,
                dispatcher.requests.filter { it.path == path }.count())
    }

    fun requestTo(path: String, test: RecordedRequestTester.() -> Unit) {
        val request = dispatcher.requests.find { it.path == path }
        request?.assertThat(test)
    }

}

