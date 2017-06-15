package info.hzvtc.hipixiv.net.converter

import okhttp3.ResponseBody
import retrofit2.Converter

class StringConverter : Converter<ResponseBody, String> {

    companion object{
        val INSTANCE = StringConverter()
    }

    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}