package info.hzvtc.hipixiv.vm

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.BindingActivity

abstract class BaseViewModel<V : BindingActivity<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    fun setView(view : V){
        this.mView = view
        this.mBind = mView.getBinding()
        initViewModel()
    }

    abstract fun initViewModel()
}
