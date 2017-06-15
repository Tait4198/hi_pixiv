package info.hzvtc.hipixiv.data

import android.app.ProgressDialog
import android.content.Context
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.pojo.token.OAuthToken
import info.hzvtc.hipixiv.util.AppUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

class Account @Inject constructor(val userPreferences: UserPreferences, val oAuthService: OAuthService) {

    fun login(username : String,password : String, progressDialog: ProgressDialog,context: Context) : Observable<OAuthToken> {
        return Observable.just(AppUtil.isNetworkConnected(context))
                .filter({ isConnected -> isConnected})
                .filter({ !username.isEmpty() && !password.isEmpty()})
                .doOnNext({
                    progressDialog.isIndeterminate = false
                    progressDialog.setMessage(context.getString(R.string.login_authenticate))
                    progressDialog.show() })
                .observeOn(Schedulers.io())
                .flatMap({ oAuthService.postAuthToken(AppKV.CLIENT_ID, AppKV.CLIENT_SECRET,
                        AppKV.GRANT_TYPE, username, password,AppKV.DEVICE_TOKEN,true)})
                .observeOn(AndroidSchedulers.mainThread())
    }


}