package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PixivUser(
        @SerializedName("id") @Expose val userId : Int,
        @SerializedName("name") @Expose val name : String,
        @SerializedName("account") @Expose val account : String,
        @SerializedName("profile_image_urls") @Expose val profile : PixivUserProfile,
        @SerializedName("is_followed") @Expose val isFollowed : Boolean
)
