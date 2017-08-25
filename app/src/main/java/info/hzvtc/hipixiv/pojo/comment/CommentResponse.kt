package info.hzvtc.hipixiv.pojo.comment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommentResponse(
        @SerializedName("total_comments") @Expose val totalComments : Int,
        @SerializedName("comments") @Expose val comments : MutableList<Comment> = ArrayList(),
        @SerializedName("next_url") @Expose val nextUrl : String = ""
)