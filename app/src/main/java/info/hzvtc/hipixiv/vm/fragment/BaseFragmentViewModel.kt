package info.hzvtc.hipixiv.vm

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.MainActivity
import info.hzvtc.hipixiv.view.fragment.BaseFragment

abstract class BaseFragmentViewModel<V : BaseFragment<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    fun setView(view : V){
        this.mView = view
        this.mBind = view.getBinding()
        initViewModel()
    }

    fun getParent() : MainActivity?{
        if(mView.activity is MainActivity){
            return mView.activity as MainActivity
        }
        return null
    }

    abstract fun runView()

    abstract fun initViewModel()
}
