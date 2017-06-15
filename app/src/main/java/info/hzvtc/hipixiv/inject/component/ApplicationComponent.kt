package info.hzvtc.hipixiv.inject.component

import android.content.Context
import dagger.Component
import info.hzvtc.hipixiv.App
import info.hzvtc.hipixiv.inject.annotation.ApplicationContext
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.net.RetrofitManager
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.net.ApiService
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(app : App)

    @ApplicationContext
    fun context() : Context

    fun UserPreferences() : UserPreferences

    fun retrofitManager() : RetrofitManager

    fun oAuthService() : OAuthService

    fun apiService() : ApiService
}