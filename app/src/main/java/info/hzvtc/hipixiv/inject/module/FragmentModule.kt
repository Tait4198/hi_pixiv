package info.hzvtc.hipixiv.inject.module

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(fragment: Fragment) {
    private val mFragment  = fragment

    @Provides
    fun providerFragment() : Fragment = mFragment
}