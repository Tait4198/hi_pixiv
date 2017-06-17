package info.hzvtc.hipixiv.inject.component

import dagger.Component
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.inject.module.FragmentModule
import info.hzvtc.hipixiv.view.fragment.IllustFragment

@Component(dependencies = arrayOf(ApplicationModule::class),modules = arrayOf(ActivityModule::class,FragmentModule::class))
interface FragmentComponent {
    fun inject(IllustFragment: IllustFragment)
}
