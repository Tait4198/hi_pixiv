package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
        @SerializedName("search_auto_complete_keywords") @Expose val keywords : MutableList<String> = ArrayList()
)