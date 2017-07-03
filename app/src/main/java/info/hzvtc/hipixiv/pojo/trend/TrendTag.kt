package info.hzvtc.hipixiv.pojo.trend

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.illust.Illust

data class TrendTag(
        @SerializedName("tag") @Expose val tag : String,
        @SerializedName("illust") @Expose val illust : Illust
)