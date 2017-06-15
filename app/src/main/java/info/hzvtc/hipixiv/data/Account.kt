package info.hzvtc.hipixiv.data

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.net.OAuthService
import info.hzvtc.hipixiv.pojo.User
import info.hzvtc.hipixiv.pojo.token.OAuthResponse
import info.hzvtc.hipixiv.pojo.token.OAuthToken
import info.hzvtc.hipixiv.util.AppUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

class Account @Inject constructor(val userPref: UserPreferences, val oAuthService: OAuthService) {

    var isFirst = true

    fun login(username : String,password : String, progressDialog: ProgressDialog,context: Context) : Observable<OAuthToken> {
        return Observable.just(AppUtil.isNetworkConnected(context))
                .filter({ isConnected -> isConnected})
                .filter({ !username.isEmpty() && !password.isEmpty()})
                .doOnNext({
                    progressDialog.isIndeterminate = false
                    progressDialog.setMessage(context.getString(R.string.login_authenticate))
                    progressDialog.show()
                })
                .observeOn(Schedulers.io())
                .flatMap({
                    oAuthService.postAuthToken(AppKV.CLIENT_ID, AppKV.CLIENT_SECRET,
                        AppKV.GRANT_TYPE_PASSWORD, username, password,AppKV.DEVICE_TOKEN,true)
                })
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun obsToken(context: Context) : Observable<OAuthToken>{
        val refresh = oAuthService.postRefreshAuthToken(AppKV.CLIENT_ID,AppKV.CLIENT_SECRET,
                AppKV.GRANT_TYPE_REFRESH,userPref.refreshToken?:"", userPref.deviceToken?:"",true)
                .doOnNext({
                    token -> saveTokenResponse(token.oAuthResponse)
                })

        val obs = Observable.just(userPref.expires)
                .observeOn(Schedulers.io())
                .flatMap({
                    expires ->
                    if(expires?:0 <= AppUtil.getNowTimestamp() && AppUtil.isNetworkConnected(context)){
                        return@flatMap refresh
                    }else{
                        return@flatMap Observable.just(getNowToken())
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({
                    if(!AppUtil.isNetworkConnected(context) && isFirst){
                        Toast.makeText(context,context.getString(R.string.app_no_network)
                                ,Toast.LENGTH_LONG).show()
                        isFirst = false
                    }else if(AppUtil.isNetworkConnected(context) && !isFirst){
                        Toast.makeText(context,context.getString(R.string.app_recovery_network)
                                ,Toast.LENGTH_LONG).show()
                    }
                })
        return obs
    }

    fun saveTokenResponse(oAuthResponse: OAuthResponse){
        userPref.isLogin = true
        userPref.accessToken = oAuthResponse.accessToken
        userPref.refreshToken = oAuthResponse.refreshToken
        userPref.deviceToken = oAuthResponse.deviceToken
        userPref.expires = oAuthResponse.expiresIn?.plus(AppUtil.getNowTimestamp())
        userPref.profileUrl = oAuthResponse.user?.profiles?.medium
        userPref.id = oAuthResponse.user?.id
        userPref.account = oAuthResponse.user?.account
        userPref.mailAddress = oAuthResponse.user?.mailAddress
        userPref.isPremium = oAuthResponse.user?.isPremium
        userPref.xRestrict = oAuthResponse.user?.xRestrict
        userPref.isMailAuthorized = oAuthResponse.user?.isMailAuthorized
    }

    fun getNowToken() : OAuthToken{
        val oAuthResponse = OAuthResponse(userPref.accessToken?:"",0,null,null
                ,userPref.refreshToken?:"",null,userPref.deviceToken?:"")
        return OAuthToken(false,oAuthResponse,null)
    }
}