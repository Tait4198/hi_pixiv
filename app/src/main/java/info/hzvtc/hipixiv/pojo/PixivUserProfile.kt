package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PixivUserProfile(
        @SerializedName("medium") @Expose val medium : String
)
