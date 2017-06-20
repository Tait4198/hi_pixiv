package info.hzvtc.hipixiv.inject.component

import android.content.Context
import dagger.Component
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.inject.annotation.ActivityContext
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.net.RetrofitManager
import info.hzvtc.hipixiv.view.LauncherActivity
import info.hzvtc.hipixiv.view.LoginActivity
import info.hzvtc.hipixiv.view.MainActivity
import javax.inject.Singleton

@Singleton
@Component(dependencies = arrayOf(ApplicationModule::class),modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(launcherActivity: LauncherActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)

    @ActivityContext
    fun context() : Context

    fun retrofitManager() : RetrofitManager

    fun oAuthService() : OAuthService

    fun apiService() : ApiService
}
