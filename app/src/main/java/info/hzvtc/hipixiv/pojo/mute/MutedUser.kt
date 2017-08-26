package info.hzvtc.hipixiv.pojo.mute

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.PixivUser

class MutedUser(
        @SerializedName("user") @Expose val user : PixivUser,
        @SerializedName("is_premium_slot") @Expose val slot : Boolean = false
)