package info.hzvtc.hipixiv.view

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivitySearchBinding
import info.hzvtc.hipixiv.vm.SearchViewModel
import javax.inject.Inject

class SearchActivity : BindingActivity<ActivitySearchBinding>(),RootActivity {

    @Inject
    lateinit var viewModel : SearchViewModel

    private var isShowFab = false

    override fun getLayoutId(): Int = R.layout.activity_search

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        mBinding.searchView.setOnHomeActionClickListener{ onBackPressed() }
        viewModel.setView(this)
    }

    fun setFabVisible(status : Boolean,isShow : Boolean){
        isShowFab = isShow
        if(status) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun showFab(status : Boolean){
        if(isShowFab){
            if(status) mBinding.fab.show() else mBinding.fab.hide()
        }
    }

    override fun getRootView(): CoordinatorLayout {
        return mBinding.rootView
    }

    fun searchByTrendTag(tag : String){
        viewModel.searchByTrendTag(tag)
    }
}