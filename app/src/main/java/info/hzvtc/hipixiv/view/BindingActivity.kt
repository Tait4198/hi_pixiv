package info.hzvtc.hipixiv.view

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View

abstract class BindingActivity<T : ViewDataBinding> : BaseActivity(){
    lateinit var mBinding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView : View = layoutInflater.inflate(getLayoutId(),null,false)
        mBinding = DataBindingUtil.bind(rootView)
        setContentView(rootView)
        initView(savedInstanceState)
    }

    fun getBinding() = mBinding

    @LayoutRes
    abstract fun getLayoutId() : Int

    abstract fun initView(savedInstanceState: Bundle?)
}