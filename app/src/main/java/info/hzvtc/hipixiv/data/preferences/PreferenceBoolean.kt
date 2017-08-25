package info.hzvtc.hipixiv.data.preferences

import info.hzvtc.hipixiv.data.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceBoolean(private val defaultValue: Boolean?) : ReadWriteProperty<UserPreferences, Boolean?> {
    override fun getValue(thisRef: UserPreferences, property: KProperty<*>): Boolean? {
        return thisRef.preferences.getBoolean(property.name, defaultValue ?: false)
    }

    override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: Boolean?) {
        thisRef.preferences.edit().putBoolean(property.name, value ?: false).apply()
    }
}