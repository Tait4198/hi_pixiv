package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences

object PreferenceDelegates {
    fun string(defaultValue: String? = null): kotlin.properties.ReadWriteProperty<UserPreferences, String?> {
        return PreferenceString(defaultValue)
    }

    fun boolean(defaultValue: Boolean? = false) : kotlin.properties.ReadWriteProperty<UserPreferences, Boolean?> {
        return PreferenceBoolean(defaultValue)
    }

    fun int(defaultValue: Int? = 0) : kotlin.properties.ReadWriteProperty<UserPreferences, Int?> {
        return PreferenceInt(defaultValue)
    }

    fun long(defaultValue: Long? = 0) : kotlin.properties.ReadWriteProperty<UserPreferences, Long?> {
        return PreferenceLong(defaultValue)
    }
}