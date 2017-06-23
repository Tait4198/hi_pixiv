package info.hzvtc.hipixiv.pojo.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserResponse(
        @SerializedName("user_previews") @Expose val userPreviews : MutableList<UserPreview> = ArrayList<UserPreview>(),
        @SerializedName("next_url") @Expose val nextUrl : String = ""
)
