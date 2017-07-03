package info.hzvtc.hipixiv.vm.fragment

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.RootActivity
import info.hzvtc.hipixiv.view.fragment.BaseFragment

abstract class BaseFragmentViewModel<V : BaseFragment<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    fun setView(view : V){
        this.mView = view
        this.mBind = view.getBinding()
        initViewModel()
    }

    fun getParent() : RootActivity?{
        if(mView.activity is RootActivity) {
            return mView.activity as RootActivity
        }
        return null
    }

    abstract fun runView()

    abstract fun initViewModel()
}
