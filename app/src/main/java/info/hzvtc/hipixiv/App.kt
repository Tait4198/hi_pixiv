package info.hzvtc.hipixiv

import android.app.Application
import android.content.Context
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import info.hzvtc.hipixiv.inject.component.ApplicationComponent
import info.hzvtc.hipixiv.inject.component.DaggerApplicationComponent
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.net.interceptor.ImageInterceptor
import info.hzvtc.hipixiv.net.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class App : Application() {

    private val mAppComponent : ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
    }

    override fun onCreate() {
        super.onCreate()
        mAppComponent.inject(this)
        initFresco()
    }

    private fun initFresco(){
        val client = OkHttpClient.Builder()
                .addInterceptor(ImageInterceptor())
                .addInterceptor(LoggingInterceptor())
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
        val cache = DiskCacheConfig.newBuilder(this)
                .setMaxCacheSize(256 * 1024 * 1024)
                .build()
        val config = OkHttpImagePipelineConfigFactory
                .newBuilder(this,client)
                .setMainDiskCacheConfig(cache)
                .build()
        Fresco.initialize(this,config)
    }

    companion object {
        fun getApp(context: Context) : App = context.applicationContext as App
    }
}
