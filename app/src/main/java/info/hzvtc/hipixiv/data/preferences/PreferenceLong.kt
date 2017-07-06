package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceLong(val defaultValue: Long?) : ReadWriteProperty<UserPreferences, Long?> {
    override fun getValue(thisRef: UserPreferences, property: KProperty<*>): Long? {
        return thisRef.preferences.getLong(property.name, defaultValue ?: 0)
    }

    override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: Long?) {
        thisRef.preferences.edit().putLong(property.name, value ?: 0).apply()
    }
}