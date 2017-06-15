package info.hzvtc.hipixiv.view


import android.support.v7.app.AppCompatActivity
import info.hzvtc.hipixiv.App
import info.hzvtc.hipixiv.inject.component.ActivityComponent
import info.hzvtc.hipixiv.inject.component.DaggerActivityComponent
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule

abstract class BaseActivity : AppCompatActivity() {
    val component : ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .applicationModule(ApplicationModule(App.getApp(this)))
                .activityModule(ActivityModule(this))
                .build()
    }
}
