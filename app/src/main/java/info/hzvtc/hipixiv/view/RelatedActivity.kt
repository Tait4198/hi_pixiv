package info.hzvtc.hipixiv.view

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityRelatedBinding
import info.hzvtc.hipixiv.vm.RelatedViewModel
import javax.inject.Inject

class RelatedActivity : BindingActivity<ActivityRelatedBinding>() {

    @Inject
    lateinit var viewModel : RelatedViewModel

    override fun getLayoutId(): Int = R.layout.activity_related

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        mBinding.layoutToolbar.toolbar.title = getString(R.string.content_related)
        setSupportActionBar(mBinding.layoutToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.layoutToolbar.toolbar.setNavigationOnClickListener({onBackPressed()})
        viewModel.setView(this)
    }
}