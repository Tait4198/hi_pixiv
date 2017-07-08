package info.hzvtc.hipixiv.data

import android.os.Build
import info.hzvtc.hipixiv.util.AppUtil

class AppKV{
    companion object {
        val CONTENT_TYPE_KEY = "Content-Type"
        val CONTENT_TYPE = "application/x-www-form-urlencoded"
        val CONTENT_TYPE_UTF8 = "application/x-www-form-urlencoded;charset=UTF-8"

        val ACCEPT_LANGUAGE_KEY = "Accept-Language"
        val ACCEPT_LANGUAGE = AppUtil.getLocalLanguage()

        val P_APP_VERSION_KEY = "App-Version"
        val P_APP_VERSION = "5.0.63"

        val APP_OS_VERSION_KEY = "App-OS-Version"
        val APP_OS_VERSION = Build.VERSION.RELEASE!!

        val APP_OS_KEY = "App-OS"
        val APP_OS = "android"

        val USER_AGENT_KEY = "User-Agent"
        val USER_AGENT = "PixivAndroidApp/" + P_APP_VERSION +
                " (Android " + APP_OS_VERSION + "; " + Build.MODEL + ")"

        val ACCEPT_ENCODING_KEY = "Accept-Encoding"
        val ACCEPT_ENCODING_GZIP = "gzip"
        val ACCEPT_ENCODING_IDE = "identity"

        val REFERER_KEY = "Referer"
        val REFERER = "https://app-api.pixiv.net/"

        val CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
        val CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
        val GRANT_TYPE_PASSWORD = "password"
        val GRANT_TYPE_REFRESH = "refresh_token"
        val DEVICE_TOKEN = "pixiv"
    }
}
