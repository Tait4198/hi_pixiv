package info.hzvtc.hipixiv.pojo.pixivision

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class PixivisionResopnse(
        @SerializedName("spotlight_articles") @Expose val content : MutableList<Pixivision> = ArrayList<Pixivision>(),
        @SerializedName("next_url") @Expose val nextUrl : String = ""
)