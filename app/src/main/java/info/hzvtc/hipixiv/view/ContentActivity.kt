package info.hzvtc.hipixiv.view

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityContentBinding
import info.hzvtc.hipixiv.vm.ContentViewModel
import javax.inject.Inject

class ContentActivity : BindingActivity<ActivityContentBinding>() {

    @Inject
    lateinit var viewModel : ContentViewModel

    override fun getLayoutId(): Int = R.layout.activity_content

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbar.setNavigationOnClickListener({onBackPressed()})
    }
}