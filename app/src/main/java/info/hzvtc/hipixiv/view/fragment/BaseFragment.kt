package info.hzvtc.hipixiv.view.fragment

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import info.hzvtc.hipixiv.App
import info.hzvtc.hipixiv.inject.component.DaggerFragmentComponent
import info.hzvtc.hipixiv.inject.component.FragmentComponent
import info.hzvtc.hipixiv.inject.module.ActivityModule
import info.hzvtc.hipixiv.inject.module.ApplicationModule
import info.hzvtc.hipixiv.inject.module.FragmentModule
import info.hzvtc.hipixiv.vm.fragment.ViewModelData

abstract class BaseFragment<out T : ViewDataBinding> : Fragment() {
    val component : FragmentComponent by lazy {
        DaggerFragmentComponent.builder()
                .applicationModule(ApplicationModule(App.getApp(this.context)))
                .activityModule(ActivityModule(this.activity))
                .fragmentModule(FragmentModule(this))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    abstract fun getBinding() : T
}