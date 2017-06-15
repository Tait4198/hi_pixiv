package info.hzvtc.hipixiv.inject.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import info.hzvtc.hipixiv.inject.annotation.ApplicationContext
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.net.RetrofitManager
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.net.ApiService

@Module
class ApplicationModule(application: Application) {
    private val mApplication: Application = application

    @Provides
    fun provideApplication(): Application = mApplication

    @Provides
    @ApplicationContext
    fun provideContext(): Context = mApplication

    @Provides
    fun provideUserPreferences() : UserPreferences = UserPreferences(mApplication)

    @Provides
    fun provideRetrofitManager(): RetrofitManager = RetrofitManager(mApplication)

    @Provides
    fun provideOAuthService(): OAuthService = provideRetrofitManager().newOAuthService()

    @Provides
    fun provideApiService() : ApiService = provideRetrofitManager().newApiService()
}
