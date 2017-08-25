package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceInt(private val defaultValue: Int?) : ReadWriteProperty<UserPreferences, Int?> {
    override fun getValue(thisRef: UserPreferences, property: KProperty<*>): Int? {
        return thisRef.preferences.getInt(property.name, defaultValue ?: 0)
    }

    override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: Int?) {
        thisRef.preferences.edit().putInt(property.name, value ?: 0).apply()
    }
}