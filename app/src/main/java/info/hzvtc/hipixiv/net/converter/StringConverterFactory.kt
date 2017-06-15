package info.hzvtc.hipixiv.net.converter

import okhttp3.ResponseBody

class StringConverterFactory : retrofit2.Converter.Factory(){

    companion object{
        val INSTANCE = info.hzvtc.hipixiv.net.converter.StringConverterFactory()
        fun create(): info.hzvtc.hipixiv.net.converter.StringConverterFactory {
            return info.hzvtc.hipixiv.net.converter.StringConverterFactory.Companion.INSTANCE
        }
    }

    override fun responseBodyConverter(type: java.lang.reflect.Type?, annotations: Array<Annotation>?, retrofit: retrofit2.Retrofit?):
            retrofit2.Converter<ResponseBody, *>? {
        if (type === String::class.java) {
            return StringConverter.Companion.INSTANCE
        }
        return null
    }
}
