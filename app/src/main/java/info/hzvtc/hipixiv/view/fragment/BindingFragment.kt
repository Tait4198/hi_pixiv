package info.hzvtc.hipixiv.view.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BindingFragment<T : ViewDataBinding> : BaseFragment() {
    lateinit var mBinding : T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View = inflater.inflate(getLayoutId(),container,false)
        mBinding = DataBindingUtil.bind(rootView)
        return initView(mBinding)
    }

    fun getBinding() = mBinding

    @LayoutRes
    abstract fun getLayoutId() : Int

    abstract fun initView(binding : T) : View
}