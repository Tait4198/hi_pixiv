package info.hzvtc.hipixiv.vm

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.fragment.BindingFragment

abstract class BaseFragmentViewModel<V : BindingFragment<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    fun setView(view : V){
        this.mView = view
        this.mBind = view.getBinding()
        initViewModel()
    }

    abstract fun initViewModel()
}
