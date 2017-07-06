package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences
import kotlin.properties.ReadWriteProperty

object PreferenceDelegates {
    fun string(defaultValue: String? = null): ReadWriteProperty<UserPreferences, String?> {
        return PreferenceString(defaultValue)
    }

    fun boolean(defaultValue: Boolean? = false) : ReadWriteProperty<UserPreferences, Boolean?> {
        return PreferenceBoolean(defaultValue)
    }

    fun int(defaultValue: Int? = 0) : ReadWriteProperty<UserPreferences, Int?> {
        return PreferenceInt(defaultValue)
    }

    fun long(defaultValue: Long? = 0) : ReadWriteProperty<UserPreferences, Long?> {
        return PreferenceLong(defaultValue)
    }
}