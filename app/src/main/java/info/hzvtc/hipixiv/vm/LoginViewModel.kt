package info.hzvtc.hipixiv.vm

import android.app.ProgressDialog
import android.content.Intent
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.ActivityLoginBinding
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.LoginActivity
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class LoginViewModel @Inject constructor(val account: Account) : BaseViewModel<LoginActivity,ActivityLoginBinding>() {

    val ERR_UNKNOWN = 0
    val ERR_NO_NETWORK = 1
    val ERR_SERVICE = 2

    private lateinit var subscribe : Disposable
    private val progressDialog : ProgressDialog by lazy {
        ProgressDialog(mView, R.style.AppTheme_Dialog)
    }

    override fun initViewModel() {
        mBind.viewModel = this
    }

    fun doLogin(@Suppress("UNUSED_PARAMETER") view : View){
        val username = mBind.inputUsername.text.toString()
        val password = mBind.inputPassword.text.toString()

        progressDialog.isIndeterminate = false
        progressDialog.setMessage(mView.getString(R.string.login_authenticate))
        progressDialog.setOnCancelListener({
            subscribe.dispose()
            if(subscribe.isDisposed)
                AppMessage.toastMessageShort(R.string.login_canceled,mView)
            else
                AppMessage.toastMessageShort(R.string.login_cancel_failed,mView)
        })

        subscribe = account.obsLogin(username,password,progressDialog,mView)
                .subscribe({
                    (hasError, oAuthResponse, errors) ->
                    if(hasError){
                        showMaterialDialog(errors.errorSystem.message,ERR_SERVICE)
                    }else{
                        account.saveTokenResponse(oAuthResponse)
                    }
                },{
                    _ -> progressDialog.dismiss()
                    showMaterialDialog(mView.getString(R.string.app_login_failed),ERR_UNKNOWN)
                },{
                    progressDialog.dismiss()
                    if(!AppUtil.isNetworkConnected(mView))
                        showMaterialDialog(mView.getString(R.string.app_no_network_set),ERR_NO_NETWORK)
                    if(username.isEmpty())
                        mBind.inputUsername.error = mView.getString(R.string.login_username_no_input)
                    if(password.isEmpty())
                        mBind.inputPassword.error = mView.getString(R.string.login_password_no_input)
                    if(account.isLogin()?:false){
                        val intent = Intent(mView.getString(R.string.activity_main))
                        ActivityCompat.startActivity(mView, intent, null)
                    }
                })
    }

    fun showMaterialDialog(msg : String,code : Int){
        MaterialDialog.Builder(mView)
                .title(mView.getString(R.string.login_failed))
                .content(msg)
                .positiveText(mView.getString(R.string.app_dialog_ok))
                .onPositive({ _,_ -> positiveEvent(code) })
                .negativeText(mView.getString(R.string.app_dialog_cancel))
                .show()
    }

    fun positiveEvent(errCode : Int){
        when(errCode){
            ERR_NO_NETWORK ->{
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ActivityCompat.startActivity(mView, intent, null)
            }
        }
    }
}
