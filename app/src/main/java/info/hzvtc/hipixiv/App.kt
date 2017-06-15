package info.hzvtc.hipixiv

import android.app.Application
import android.content.Context
import info.hzvtc.hipixiv.inject.component.ApplicationComponent
import info.hzvtc.hipixiv.inject.component.DaggerApplicationComponent
import info.hzvtc.hipixiv.inject.module.ApplicationModule


class App : Application() {

    private val mAppComponent : ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()
    }

    override fun onCreate() {
        super.onCreate()
        mAppComponent.inject(this)
    }

    fun getApplicationComponent() : ApplicationComponent = mAppComponent

    companion object {
        fun getApp(context: Context) : App = context.applicationContext as App
    }
}
