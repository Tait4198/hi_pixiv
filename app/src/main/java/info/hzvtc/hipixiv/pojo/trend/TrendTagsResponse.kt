package info.hzvtc.hipixiv.pojo.trend

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TrendTagsResponse(
        @SerializedName("trend_tags") @Expose val trendTags : MutableList<TrendTag> = ArrayList<TrendTag>()
)
