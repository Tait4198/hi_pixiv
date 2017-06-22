package info.hzvtc.hipixiv.view.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class LazyFragment<T : ViewDataBinding> : BaseFragment(){

    lateinit var mBinding : T
    private var isPrepared: Boolean = false
    private var isFirstResume = true
    private var isFirstVisible = true
    private var isFirstInvisible = true
    private var isUserVisible = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initPrepare()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View = inflater.inflate(getLayoutId(),container,false)
        mBinding = DataBindingUtil.bind(rootView)
        return mBinding.root
    }


    @LayoutRes
    abstract fun getLayoutId() : Int

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        }
        if (userVisibleHint) {
            onUserVisible()
        }
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            onUserInvisible()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isUserVisible = isVisibleToUser
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false
                initPrepare()
            } else {
                onUserVisible()
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false
                onFirstUserInvisible()
            } else {
                onUserInvisible()
            }
        }
    }

    @Synchronized private fun initPrepare() {
        if (isPrepared) {
            onFirstUserVisible()
        } else {
            isPrepared = true
        }
    }

    open fun onFirstUserVisible() {
        //
    }

    open fun onUserVisible() {
        //
    }

    open fun onFirstUserInvisible() {
        //
    }

    open fun onUserInvisible() {
        //
    }

    open fun isUserVisible() = isUserVisible
}
