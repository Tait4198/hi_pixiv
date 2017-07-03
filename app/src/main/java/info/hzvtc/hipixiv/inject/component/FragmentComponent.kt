package info.hzvtc.hipixiv.inject.component

import dagger.Component
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.inject.module.FragmentModule
import info.hzvtc.hipixiv.view.fragment.*

@Component(dependencies = arrayOf(ApplicationModule::class),modules = arrayOf(ActivityModule::class,FragmentModule::class))
interface FragmentComponent {
    fun inject(IllustFragment: IllustFragment)
    fun inject(illustLazyFragment: IllustLazyFragment)
    fun inject(userLazyFragment: UserLazyFragment)
    fun inject(pixivisionLazyFragment: PixivisionLazyFragment)
    fun inject(trendTagLazyFragment: TrendTagLazyFragment)
    fun inject(illustSearchFragment: IllustSearchFragment)
    fun inject(userSearchFragment: UserSearchFragment)
}
