package info.hzvtc.hipixiv.data.preferences

class PreferenceInt(val defaultValue: Int?) : kotlin.properties.ReadWriteProperty<info.hzvtc.hipixiv.data.UserPreferences, Int?> {
    override fun getValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>): Int? {
        return thisRef.preferences.getInt(property.name, defaultValue ?: 0)
    }

    override fun setValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>, value: Int?) {
        thisRef.preferences.edit().putInt(property.name, value ?: 0).apply()
    }
}