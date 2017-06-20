package info.hzvtc.hipixiv.view

import android.content.Intent
import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityLoginBinding
import info.hzvtc.hipixiv.vm.LoginViewModel
import javax.inject.Inject

class LoginActivity : BindingActivity<ActivityLoginBinding>(){

    @Inject
    lateinit var viewModel : LoginViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}
