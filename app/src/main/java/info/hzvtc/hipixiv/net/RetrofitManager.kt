package info.hzvtc.hipixiv.net

import android.content.Context
import com.google.gson.GsonBuilder
import info.hzvtc.hipixiv.net.interceptor.ApiInterceptor
import info.hzvtc.hipixiv.net.interceptor.CacheInterceptor
import info.hzvtc.hipixiv.net.interceptor.LoggingInterceptor
import info.hzvtc.hipixiv.net.interceptor.OAuthInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class RetrofitManager(context : Context){
    private val OAUTH_URL = "https://oauth.secure.pixiv.net"
    private val API_URL = "https://app-api.pixiv.net"
    private val oAuthInterceptor = OAuthInterceptor(context)
    private val apiInterceptor = ApiInterceptor(context)
    private val loggingInterceptor = LoggingInterceptor()
    private val cacheInterceptor = CacheInterceptor(context)
    private val adapter = RxJava2CallAdapterFactory.create()
    private val converter = GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())

    private val oAuthClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .addNetworkInterceptor(oAuthInterceptor)
                .addInterceptor(oAuthInterceptor)
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(8, TimeUnit.SECONDS)
                .build()
    }
    private val apiClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor(apiInterceptor)
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(loggingInterceptor)
                .cache(Cache(File(context.cacheDir.toString(), "cache"), 128 * 1024 * 1024))
                .retryOnConnectionFailure(true)
                .connectTimeout(8,TimeUnit.SECONDS)
                .build()
    }

    fun newOAuthService() : OAuthService{
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(OAUTH_URL)
                .client(oAuthClient)
                .addCallAdapterFactory(adapter)
                .addConverterFactory(converter)
                .build()
        return retrofit.create(OAuthService::class.java)
    }

    fun newApiService() : ApiService{
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .client(apiClient)
                .addCallAdapterFactory(adapter)
                .addConverterFactory(converter)
                .build()
        return retrofit.create(ApiService::class.java)
    }
}
