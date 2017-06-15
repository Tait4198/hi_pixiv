package info.hzvtc.hipixiv.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import info.hzvtc.hipixiv.R

class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        val intent = Intent(getString(R.string.activity_login))
        ActivityCompat.startActivity(this, intent, null)
    }
}
