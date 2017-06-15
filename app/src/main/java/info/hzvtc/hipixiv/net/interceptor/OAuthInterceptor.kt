package info.hzvtc.hipixiv.net.interceptor

import android.content.Context
import info.hzvtc.hipixiv.data.AppKV
import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptor(val context: Context) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .addHeader(AppKV.USER_AGENT_KEY, AppKV.USER_AGENT)
                .addHeader(AppKV.CONTENT_TYPE_KEY, AppKV.CONTENT_TYPE)
                .addHeader(AppKV.ACCEPT_LANGUAGE_KEY, AppKV.ACCEPT_LANGUAGE)
                .addHeader(AppKV.APP_OS_KEY, AppKV.APP_OS)
                .addHeader(AppKV.APP_OS_VERSION_KEY, AppKV.APP_OS_VERSION)
                .addHeader(AppKV.P_APP_VERSION_KEY, AppKV.P_APP_VERSION)
                .build()
        val response = chain.proceed(request)
        if(response.code() == 400){
            return response.newBuilder().message("OK").code(200).build()
        }else{
            return response
        }
    }
}
