package info.hzvtc.hipixiv.vm

import android.databinding.ViewDataBinding
import info.hzvtc.hipixiv.view.BindingActivity
import info.hzvtc.hipixiv.view.fragment.BindingFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragmentViewModel<V : BindingFragment<T>,T : ViewDataBinding>{
    protected lateinit var mView : V
    protected lateinit var mBind : T

    protected val compositeDisposable : CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun setView(view : V){
        this.mView = view
        this.mBind = view.getBinding()
        initViewModel()
    }

    abstract fun initViewModel()

    fun clear(){
        compositeDisposable.clear()
    }
}
