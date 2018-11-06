package com.filestack.internal

import com.filestack.Config
import com.filestack.StorageOptions
import com.nhaarman.mockitokotlin2.mock

class UploadStartFuncTest {

    val uploadService = mock<UploadService>()

/*    @Test
    fun `includes upload params`() {
        val upload = upload()
        val startResponse = Model.startResponse()
        val func = UploadStartFunc(uploadService, upload)
        whenever(uploadService.start(any())).thenReturn(
                MockResponse.success(startResponse)
        )

        func.call()

        startResponse.uploadParams.entries.forEach {
            assertTrue(upload.baseParams.containsKey(it.key))
            val body = upload.baseParams[it.key]
            assertEquals(it.value.contentType(), body?.contentType())
            assertEquals(it.value.contentLength(), body?.contentLength())
        }
    }

    @Test
    fun `sets proper number of e-tags`() {
        val upload = upload(inputSize = 10 * 1024 * 1024)
        val startResponse = Model.startResponse()
        val func = UploadStartFunc(uploadService, upload)
        whenever(uploadService.start(any())).thenReturn(
                MockResponse.success(startResponse)
        )

        func.call()

        assertEquals(
                10 * 1024 * 1024 / Upload.REGULAR_PART_SIZE,
                upload.etags?.size)
    }

    @Test
    fun `respects intelligent ingestion settings - set to false`() {
        val upload = upload(intel = false)
        val startResponse = Model.startResponse()
        val func = UploadStartFunc(uploadService, upload)
        whenever(uploadService.start(any())).thenReturn(
                MockResponse.success(startResponse)
        )

        func.call()

        assertFalse(upload.baseParams.containsKey("multipart"))
        assertEquals(Upload.REGULAR_PART_SIZE, upload.partSize)
    }

    @Test
    fun `respects intelligent ingestion settings - set to true and server supports it`() {
        val upload = upload(intel = true)
        val startResponse = Model.startResponse(uploadType = "intelligent_ingestion")
        val func = UploadStartFunc(uploadService, upload)
        whenever(uploadService.start(any())).thenReturn(
                MockResponse.success(startResponse)
        )

        func.call()

        assertTrue(upload.baseParams.containsKey("multipart"))
        assertEquals(Upload.INTELLIGENT_PART_SIZE, upload.partSize)
    }

    @Test
    fun `respects intelligent ingestion settings - set to true and server does not support it`() {
        val upload = upload(intel = true)
        val startResponse = Model.startResponse(uploadType = "some_other_type")
        val func = UploadStartFunc(uploadService, upload)
        whenever(uploadService.start(any())).thenReturn(
                MockResponse.success(startResponse)
        )

        func.call()

        assertFalse(upload.baseParams.containsKey("multipart"))
        assertEquals(Upload.REGULAR_PART_SIZE, upload.partSize)
    }*/

    private fun upload(
            inputSize: Int = 0,
            intel: Boolean = false
    ): Upload {
        return Upload(
                Config(""),
                uploadService,
                null,
                inputSize,
                intel,
                StorageOptions.Builder().build()
        )
    }

}