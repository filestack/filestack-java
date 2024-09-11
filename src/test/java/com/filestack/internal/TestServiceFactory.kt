package org.filestack.internal

import com.google.gson.Gson
import okhttp3.HttpUrl

class TestServiceFactory {

    companion object {
        fun uploadService(networkClient: NetworkClient, url: HttpUrl) = UploadService(networkClient, url)
        fun baseService(networkClient: NetworkClient, url: HttpUrl) = BaseService(networkClient, url)
        fun cdnService(networkClient: NetworkClient, url: HttpUrl) = CdnService(networkClient, url)
        fun cloudService(networkClient: NetworkClient, gson: Gson, url: HttpUrl) = CloudService(networkClient, gson, url)
    }

}