package info.hzvtc.hipixiv.pojo.tag

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookmarkTag(
        @SerializedName("name") @Expose val name : String,
        @SerializedName("count") @Expose val count : Int
)