package info.hzvtc.hipixiv.pojo.illust

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class IllustResponse(
        @SerializedName("illusts") @Expose val content : List<Illust> = ArrayList<Illust>(),
        @SerializedName("ranking_illusts") @Expose val ranking : List<Illust> = ArrayList<Illust>(),
        @SerializedName("contest_exists") @Expose val isContestExists : Boolean = false,
        @SerializedName("next_url") @Expose val nextUrl : String
)
