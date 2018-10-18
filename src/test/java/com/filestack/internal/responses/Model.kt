package com.filestack.internal.responses

class Model {
    companion object {
        fun startResponse(
                uri: String = "",
                region: String = "",
                locationUrl: String = "",
                uploadId: String = "",
                uploadType: String = ""
                ): StartResponse {
            return StartResponse(uri, region, locationUrl, uploadId, uploadType)
        }
    }
}
