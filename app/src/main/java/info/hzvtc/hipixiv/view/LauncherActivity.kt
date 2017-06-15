package info.hzvtc.hipixiv.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.UserPreferences
import javax.inject.Inject

class LauncherActivity : BaseActivity() {

    @Inject
    lateinit var userPreferences : UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        val intent : Intent
        if(userPreferences.isLogin?:false){
            intent = Intent(getString(R.string.activity_main))

        }else{
            intent = Intent(getString(R.string.activity_login))
        }
        ActivityCompat.startActivity(this, intent, null)
    }
}
