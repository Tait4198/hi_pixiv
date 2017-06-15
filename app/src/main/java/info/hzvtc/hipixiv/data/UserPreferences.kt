package info.hzvtc.hipixiv.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import info.hzvtc.hipixiv.data.preferences.PreferenceDelegates

class UserPreferences(val context: Context) {
    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var isLogin by PreferenceDelegates.boolean()
    var accessToken by PreferenceDelegates.string()
    var refreshToken by PreferenceDelegates.string()
    var expires by PreferenceDelegates.long()
    var profileUrl by PreferenceDelegates.string()
    var id by PreferenceDelegates.int()
    var name by PreferenceDelegates.string()
    var account by PreferenceDelegates.string()
    var mailAddress by PreferenceDelegates.string()
    var isPremium by PreferenceDelegates.boolean()
    var xRestrict by PreferenceDelegates.int()
    var isMailAuthorized by PreferenceDelegates.boolean()
}



