package info.hzvtc.hipixiv.data.preferences

class PreferenceString(val defaultValue: String?) : kotlin.properties.ReadWriteProperty<info.hzvtc.hipixiv.data.UserPreferences, String?> {
    override fun getValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>): String? {
        return thisRef.preferences.getString(property.name, defaultValue)
    }

    override fun setValue(thisRef: info.hzvtc.hipixiv.data.UserPreferences, property: kotlin.reflect.KProperty<*>, value: String?) {
        thisRef.preferences.edit().putString(property.name, value).apply()
    }
}