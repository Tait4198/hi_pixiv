package info.hzvtc.hipixiv.net.interceptor

import android.content.Context
import info.hzvtc.hipixiv.util.AppUtil
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.CacheControl

class CacheInterceptor(val context: Context) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!AppUtil.isNetworkConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
        }
        val response = chain.proceed(request)
        if (AppUtil.isNetworkConnected(context)) {
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=0")
                    .removeHeader("Pragma")
                    .build()
        }else{
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=${7 * 24 * 60 * 60}")
                    .removeHeader("Pragma")
                    .build()
        }
        return response
    }

}
