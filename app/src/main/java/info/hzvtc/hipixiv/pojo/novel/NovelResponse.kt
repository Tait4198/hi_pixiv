package info.hzvtc.hipixiv.pojo.novel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//todo Novel
data class NovelResponse(
        @SerializedName("next_url") @Expose val nextUrl : String = ""
)