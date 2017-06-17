package info.hzvtc.hipixiv.net.interceptor

import info.hzvtc.hipixiv.data.AppKV
import okhttp3.Interceptor
import okhttp3.Response

class ImageInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader(AppKV.USER_AGENT_KEY, AppKV.USER_AGENT)
                .addHeader(AppKV.REFERER_KEY, AppKV.REFERER)
                .addHeader(AppKV.ACCEPT_ENCODING_KEY, AppKV.ACCEPT_ENCODING_GZIP)
                .build()
        return chain.proceed(request)
    }
}