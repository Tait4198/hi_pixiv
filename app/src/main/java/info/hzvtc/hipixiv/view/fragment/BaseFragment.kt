package info.hzvtc.hipixiv.view.fragment

import android.support.v4.app.Fragment
import info.hzvtc.hipixiv.App
import info.hzvtc.hipixiv.inject.component.DaggerFragmentComponent
import info.hzvtc.hipixiv.inject.component.FragmentComponent
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.inject.module.FragmentModule

abstract class BaseFragment : Fragment() {
    val component : FragmentComponent by lazy {
        DaggerFragmentComponent.builder()
                .applicationModule(ApplicationModule(App.getApp(this.context)))
                .activityModule(ActivityModule(this.activity))
                .fragmentModule(FragmentModule(this))
                .build()
    }
}