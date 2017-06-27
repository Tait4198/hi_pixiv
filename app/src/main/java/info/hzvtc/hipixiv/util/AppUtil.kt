package info.hzvtc.hipixiv.util

import android.content.Context
import java.util.*
import android.net.ConnectivityManager
import java.net.URLEncoder

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
    }
}
