package info.hzvtc.hipixiv.vm

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import info.hzvtc.hipixiv.view.fragment.BindingFragment

abstract class BaseFragmentViewModel<V : BaseFragment<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    fun setView(view : V){
        this.mView = view
        this.mBind = view.getBinding()
        initViewModel()
    }

    abstract fun runView()

    abstract fun initViewModel()


}
