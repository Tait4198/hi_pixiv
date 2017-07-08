package info.hzvtc.hipixiv.pojo.comment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.PixivUser

data class Comment(
        @SerializedName("id") @Expose val pixivId : Int,
        @SerializedName("comment") @Expose val comment : String,
        @SerializedName("date") @Expose val date : String,
        @SerializedName("user") @Expose val user : PixivUser,
        @SerializedName("parent_comment") @Expose val parentComment : Comment? = null
)