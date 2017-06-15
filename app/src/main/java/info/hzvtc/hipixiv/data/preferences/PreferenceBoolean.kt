package info.hzvtc.hipixiv.data.preferences

class PreferenceBoolean(val defaultValue: Boolean?) : kotlin.properties.ReadWriteProperty<info.hzvtc.hipixiv.data.UserPreferences, Boolean?> {
    override fun getValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>): Boolean? {
        return thisRef.preferences.getBoolean(property.name, defaultValue ?: false)
    }

    override fun setValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>, value: Boolean?) {
        thisRef.preferences.edit().putBoolean(property.name, value ?: false).apply()
    }
}