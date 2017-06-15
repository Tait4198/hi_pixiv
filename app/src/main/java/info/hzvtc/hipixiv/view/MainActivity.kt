package info.hzvtc.hipixiv.view

import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityMainBinding
import info.hzvtc.hipixiv.vm.MainViewModel
import javax.inject.Inject
import android.content.Intent
import android.util.Log
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences


class MainActivity : BindingActivity<ActivityMainBinding>() {

    @Inject
    lateinit var viewModel : MainViewModel
    @Inject
    lateinit var account : Account
    @Inject
    lateinit var pref : UserPreferences

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        component.inject(this)
        viewModel.setView(this)
        account.obsToken(this).subscribe({
            t ->
            Log.d("accessToken",t.oAuthResponse.accessToken)
            Log.d("refreshToken",t.oAuthResponse.refreshToken)
            Log.d("deviceToken",t.oAuthResponse.deviceToken)
            Log.d("deviceToken",pref.expires.toString())
        })
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

}
