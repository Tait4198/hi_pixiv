package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("profile_image_urls") @Expose val profiles : ProfileImageUrls,
        @SerializedName("id") @Expose val id : Int,
        @SerializedName("name") @Expose val name : String,
        @SerializedName("account") @Expose val account : String,
        @SerializedName("mail_address") @Expose val mailAddress : String,
        @SerializedName("is_premium") @Expose val isPremium : Boolean,
        @SerializedName("x_restrict") @Expose val xRestrict : Int,
        @SerializedName("is_mail_authorized") @Expose val isMailAuthorized : Boolean
)