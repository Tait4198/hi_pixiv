package info.hzvtc.hipixiv.inject.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import info.hzvtc.hipixiv.inject.annotation.ApplicationContext
import info.hzvtc.hipixiv.data.UserPreferences

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
    fun provideGson() : Gson = Gson()
}
