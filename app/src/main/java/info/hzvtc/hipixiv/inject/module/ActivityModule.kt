package info.hzvtc.hipixiv.inject.module

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import info.hzvtc.hipixiv.inject.annotation.ActivityContext

@Module
class ActivityModule(activity : Activity) {
    private val mActivity : Activity = activity

    @Provides
    fun provideActivity() : Activity = mActivity

    @Provides
    @ActivityContext
    fun provideContext(): Context = mActivity
}
