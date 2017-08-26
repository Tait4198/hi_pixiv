package info.hzvtc.hipixiv.pojo.mute

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.Tag

class MutedTag(
        @SerializedName("tag") @Expose val tag : Tag,
        @SerializedName("is_premium_slot") @Expose val slot : Boolean = false
)