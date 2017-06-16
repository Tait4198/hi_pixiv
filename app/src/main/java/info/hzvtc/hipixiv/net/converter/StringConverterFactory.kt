package info.hzvtc.hipixiv.net.converter

import okhttp3.ResponseBody
import retrofit2.Retrofit

class StringConverterFactory : retrofit2.Converter.Factory(){

    companion object{
        val INSTANCE = StringConverterFactory()
        fun create(): StringConverterFactory {
            return StringConverterFactory.Companion.INSTANCE
        }
    }

    override fun responseBodyConverter(type: java.lang.reflect.Type?, annotations: Array<Annotation>?, retrofit: Retrofit?):
            retrofit2.Converter<ResponseBody, *>? {
        if (type === String::class.java) {
            return StringConverter.Companion.INSTANCE
        }
        return null
    }
}
