package info.hzvtc.hipixiv.util

import android.app.Activity
import android.content.Context
import java.util.*
import android.net.ConnectivityManager



class AppUtil{
    companion object{
        /**
         * @return zh_CN,en_US,ja_JP
         */
        fun getLocalLanguage() : String = Locale.getDefault().language + "_" + Locale.getDefault().country

        fun isNetworkConnected(context : Context?) : Boolean {
            if (context != null) {
                val mConnectivityManager = context
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            }
            return false
        }

        fun getNowTimestamp() = System.currentTimeMillis()/1000


        fun getVersion(context: Context) : String{
            val packageManager = context.packageManager
            val info = packageManager.getPackageInfo(context.packageName, 0)
            return info.versionName
        }
    }
}
