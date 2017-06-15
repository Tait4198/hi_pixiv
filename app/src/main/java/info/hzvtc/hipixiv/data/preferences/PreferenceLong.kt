package info.hzvtc.hipixiv.data.preferences

class PreferenceLong(val defaultValue: Long?) : kotlin.properties.ReadWriteProperty<info.hzvtc.hipixiv.data.UserPreferences, Long?> {
    override fun getValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>): Long? {
        return thisRef.preferences.getLong(property.name, defaultValue ?: 0)
    }

    override fun setValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>, value: Long?) {
        thisRef.preferences.edit().putLong(property.name, value ?: 0).apply()
    }
}