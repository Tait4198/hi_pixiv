package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceString(val defaultValue: String?) : ReadWriteProperty<UserPreferences, String?> {
    override fun getValue(thisRef: UserPreferences, property: KProperty<*>): String? {
        return thisRef.preferences.getString(property.name, defaultValue)
    }

    override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: String?) {
        thisRef.preferences.edit().putString(property.name, value).apply()
    }
}