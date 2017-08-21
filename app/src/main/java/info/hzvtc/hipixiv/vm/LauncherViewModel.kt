package info.hzvtc.hipixiv.vm

import android.content.Intent
import android.support.v13.app.ActivityCompat
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.ActivityLauncherBinding
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.LauncherActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import io.reactivex.schedulers.Schedulers

class LauncherViewModel @Inject constructor(var userPreferences : UserPreferences): BaseViewModel<LauncherActivity, ActivityLauncherBinding>() {
    override fun initViewModel() {
        Observable.just(AppUtil.getVersion(mView))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { version -> mBind.appVersion.text = version }
                .subscribeOn(Schedulers.io())
                .map {
                    if(userPreferences.isLogin?:false){
                        return@map Intent(getString(R.string.activity_main))
                    }else{
                        return@map Intent(getString(R.string.activity_login))
                    }
                }
                .delay(500,TimeUnit.MILLISECONDS)
                .subscribe{
                    intent -> ActivityCompat.startActivity(mView, intent, null)
                }
    }
}