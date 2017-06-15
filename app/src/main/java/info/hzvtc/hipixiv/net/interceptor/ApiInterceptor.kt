package info.hzvtc.hipixiv.net.interceptor

import android.content.Context
import info.hzvtc.hipixiv.data.AppKV
import info.hzvtc.hipixiv.util.AppUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(val context : Context) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if(!AppUtil.isNetworkConnected(context)){
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }else {
            request = chain.request().newBuilder()
                    .addHeader(AppKV.USER_AGENT_KEY, AppKV.USER_AGENT)
                    .addHeader(AppKV.CONTENT_TYPE_KEY, AppKV.CONTENT_TYPE_UTF8)
                    .addHeader(AppKV.ACCEPT_LANGUAGE_KEY, AppKV.ACCEPT_LANGUAGE)
                    .addHeader(AppKV.APP_OS_KEY, AppKV.APP_OS)
                    .addHeader(AppKV.APP_OS_VERSION_KEY, AppKV.APP_OS_VERSION)
                    .addHeader(AppKV.P_APP_VERSION_KEY, AppKV.P_APP_VERSION)
                    .addHeader(AppKV.ACCEPT_ENCODING_KEY, AppKV.ACCEPT_ENCODING_GZIP)
                    .build()
        }

        val response = chain.proceed(request)
        if(AppUtil.isNetworkConnected(context)) {
            val cacheControl = request.cacheControl().toString()
            return response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", cacheControl)
                    .build()
        }else {
            return response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=${7 * 24 * 60 * 60}")
                    .build()
        }
    }

}
