package info.hzvtc.hipixiv.pojo.tag

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class BookmarkTagResponse(
        @SerializedName("bookmark_tags") @Expose val tags : MutableList<BookmarkTag> = ArrayList(),
        @SerializedName("next_url") @Expose val nextUrl : String = ""
)