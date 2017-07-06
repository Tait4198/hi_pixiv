package info.hzvtc.hipixiv.view

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityImageBinding
import info.hzvtc.hipixiv.vm.ImageViewModel
import javax.inject.Inject

class ImageActivity : BindingActivity<ActivityImageBinding>() {

    @Inject
    lateinit var viewModel : ImageViewModel

    override fun getLayoutId(): Int = R.layout.activity_image

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)
    }
}