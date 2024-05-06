package org.filestack

import org.filestack.internal.NetworkClient
import org.filestack.internal.TestServiceFactory.Companion.baseService
import org.filestack.internal.TestServiceFactory.Companion.cdnService
import org.filestack.internal.TestServiceFactory.Companion.cloudService
import org.filestack.internal.TestServiceFactory.Companion.uploadService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private const val UPLOAD_ID = "iacwRXloJVbO78XMR7vQqiiKJRIr2.geEepw4aUG"
private const val API_KEY = "iowjr230942nn2"

@Suppress("MemberVisibilityCanBePrivate")
class ClientTest {

    @get:Rule
    val server = MockWebServer()

    val okHttpClient = OkHttpClient.Builder().build()

    val gson = Gson()

    val networkClient = NetworkClient(okHttpClient, gson)
    val cdnService = cdnService(networkClient, server.url("/"))
    val baseService = baseService(networkClient, server.url("/"))
    val uploadService = uploadService(networkClient, server.url("/"))
    val cloudService = cloudService(networkClient, gson, server.url("/"))

    val config = Config(API_KEY, "policy", "signature")
    val client = Client(config, cdnService, baseService, uploadService, cloudService)

    @Test
    fun `regular upload - single part`() {
        val file = tempFile(sizeInBytes = 1024)

        server.enqueue(MockResponse().setBody("""{
                    "uri": "/filestack-uploads/${file.name}",
                    "region": "eu-west-1",
                    "upload_id": "$UPLOAD_ID",
                    "location_url": "upload-eu-west.com",
                    "upload_type": "intelligent_ingestion"
                }"""))

        server.enqueue(MockResponse().setBody("""{
                    "url": "${server.url("/s3_upload_url")}",
                    "headers": {
                        "Authorization": "s3_authorization_token",
                        "Content-Md5": "KSiCLGZTaJerQ9kDUi8zCg==",
                        "x-amz-content-sha256": "UNSIGNED-PAYLOAD",
                        "x-amz-date": "20180912T090547Z"
                    },
                    "location_url": "upload-eu-west.com"
                }"""))

        //upload to S3 returns only 200
        server.enqueue(MockResponse())

        server.enqueue(MockResponse().setBody("""{
                  "handle": "Ekf5elTQeed8SG549RP",
                  "url": "https://cdn.filestackcontent.com/Ekf5elTQeed8SG549RP",
                  "filename": "some_file.txt",
                  "size": ${file.length()},
                  "mimetype": "text/plain",
                  "status": "Complete"
                }
                """))


        val fileLink = client.upload(file.path, false)
        assertEquals("Ekf5elTQeed8SG549RP", fileLink.handle)

        server.takeRequest().assertThat {
            isPost()
            pathIs("/multipart/start")

            bodyField("apikey", API_KEY)
            bodyField("size", file.length().toString())
            bodyField("filename", file.name)
            bodyField("store_location", "s3")

            noField("multipart")
        }

        server.takeRequest().assertThat {
            isPost()
            pathIs("/multipart/upload")

            bodyField("apikey", API_KEY)
            bodyField("part", 1)
            bodyField("size", file.length().toString())
            bodyField("region", "eu-west-1")
            bodyField("store_location", "s3")
            bodyField("upload_id", UPLOAD_ID)

            noField("upload_type")
        }

        server.takeRequest().assertThat {
            isPut()
            pathIs("/s3_upload_url")

            header("Authorization", "s3_authorization_token")
            header("Content-MD5", "KSiCLGZTaJerQ9kDUi8zCg==")
            header("x-amz-content-sha256", "UNSIGNED-PAYLOAD")
            header("x-amz-date", "20180912T090547Z")
        }

        server.takeRequest().assertThat {
            isPost()
            pathIs("/multipart/complete")

            bodyField("apikey", API_KEY)
            bodyField("upload_id", UPLOAD_ID)
            bodyField("region", "eu-west-1")
        }
    }

    @Test
    fun `regular upload - few parts`() {
        val file = tempFile(sizeInBytes = 16 * 1024 * 1024)

        val dispatcher = object : RequestStoringDispatcher() {
            override fun dispatchFor(request: RecordedRequest): MockResponse =
                    when (request.path) {
                        "/multipart/start" -> MockResponse().setBody("""{
                                "uri": "/filestack-uploads/${file.name}",
                                "region": "eu-west-1",
                                "upload_id": "$UPLOAD_ID",
                                "location_url": "upload-eu-west.com",
                                "upload_type": "intelligent_ingestion"
                            }""")

                        "/multipart/upload" -> uploadResponse(request)

                        "/s3_upload_url?partNumber=1&uploadId=upload_id_for_part_1",
                        "/s3_upload_url?partNumber=2&uploadId=upload_id_for_part_2",
                        "/s3_upload_url?partNumber=3&uploadId=upload_id_for_part_3",
                        "/s3_upload_url?partNumber=4&uploadId=upload_id_for_part_4" -> MockResponse()

                        "/multipart/complete" -> MockResponse().setBody("""{
                              "handle": "Ekf5elTQeed8SG549RP",
                              "url": "https://cdn.filestackcontent.com/Ekf5elTQeed8SG549RP",
                              "filename": "some_file.txt",
                              "size": ${file.length()},
                              "mimetype": "text/plain",
                              "status": "Complete"
                            }
                            """)

                        else -> MockResponse().setResponseCode(403)
                    }

            private fun uploadResponse(request: RecordedRequest): MockResponse {
                val part = request.bodyParams()["part"]
                return MockResponse().setBody("""{
                          "url": "${server.url("/s3_upload_url")}?partNumber=$part\u0026uploadId=upload_id_for_part_$part",
                          "headers": {
                            "Authorization": "s3_authorization_token",
                            "Content-Md5": "Hp1M87g6X56soslQBkSlhQ==",
                            "x-amz-content-sha256": "UNSIGNED-PAYLOAD",
                            "x-amz-date": "20180912T145703Z"
                          },
                          "location_url": "upload-eu-west-1.filestackapi.com"
                        }
                        """)
            }
        }
        server.dispatcher = dispatcher

        val fileLink = client.upload(file.path, false)
        assertEquals("Ekf5elTQeed8SG549RP", fileLink.handle)

        dispatcher.assertThat {
            totalRequests(10)

            requestTo("/multipart/start") {
                bodyField("apikey", API_KEY);
                bodyField("filename", file.name)
                bodyField("size", file.length())
            }

            onlyOneRequest("/s3_upload_url?partNumber=1&uploadId=upload_id_for_part_1")
            onlyOneRequest("/s3_upload_url?partNumber=2&uploadId=upload_id_for_part_2")
            onlyOneRequest("/s3_upload_url?partNumber=3&uploadId=upload_id_for_part_3")
            onlyOneRequest("/s3_upload_url?partNumber=4&uploadId=upload_id_for_part_4")
        }

    }
}
