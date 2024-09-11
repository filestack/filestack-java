package org.filestack.internal

import okio.ByteString
import org.junit.Assert.assertEquals
import org.junit.Test

class HashTest {

    @Test
    fun md5() {
        val result = Hash.md5("filestack".toByteArray())!!
        assertEquals("4d9248e9ad027c6f03a90897e329ca92", ByteString.of(*result).hex())
    }

    @Test
    fun hmacSha256() {
        val result = Hash.hmacSha256("filestackKey".toByteArray(), "filestackMessage".toByteArray())
        assertEquals("3ea0712eaae796f0330814eff179dd39ab852924a7af023789ce39e3788af2f3", result)
    }
}
