package com.filestack.internal

import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Scheduler
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class UploadTest {


    val uploadService = mock<UploadService>()

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { _: Scheduler -> Schedulers.from { it.run() } }
    }

    @Test
    fun testStartUpload() {
        val upload = Upload(null, uploadService, null, 0, false, null)


    }

}
