package info.hzvtc.hipixiv.inject.module

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import info.hzvtc.hipixiv.inject.annotation.ActivityContext
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.net.RetrofitManager

@Module
class ActivityModule(activity : Activity) {
    private val mActivity : Activity = activity

    @Provides
    fun provideActivity() : Activity = mActivity

    @Provides
    @ActivityContext
    fun provideContext(): Context = mActivity

    @Provides
    fun provideRetrofitManager(): RetrofitManager = RetrofitManager(mActivity)

    @Provides
    fun provideOAuthService(): OAuthService = provideRetrofitManager().newOAuthService()

    @Provides
    fun provideApiService() : ApiService = provideRetrofitManager().newApiService()
}
