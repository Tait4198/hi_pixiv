package info.hzvtc.hipixiv.pojo.novel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Novel(
        @SerializedName("id") @Expose val novelId : Int
)