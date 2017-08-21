package info.hzvtc.hipixiv.view

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityLauncherBinding
import info.hzvtc.hipixiv.vm.LauncherViewModel
import javax.inject.Inject

class LauncherActivity : BindingActivity<ActivityLauncherBinding>() {

    @Inject
    lateinit var viewModel : LauncherViewModel

    override fun getLayoutId(): Int = R.layout.activity_launcher

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)
    }
}
