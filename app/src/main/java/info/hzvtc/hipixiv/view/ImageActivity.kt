package info.hzvtc.hipixiv.view

import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityImageBinding
import info.hzvtc.hipixiv.vm.ImageViewModel
import javax.inject.Inject

class ImageActivity : BindingActivity<ActivityImageBinding>() {

    @Inject
    lateinit var viewModel : ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int = R.layout.activity_image

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)
        mBinding.toolbar.title = ""
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbar.setNavigationOnClickListener({onBackPressed()})
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.image_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stop()
    }
}