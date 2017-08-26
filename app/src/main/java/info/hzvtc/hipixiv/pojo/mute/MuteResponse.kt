package info.hzvtc.hipixiv.pojo.mute

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MuteResponse(
        @SerializedName("muted_tags") @Expose val mutedTags : MutableList<MutedTag> = ArrayList(),
        @SerializedName("muted_users") @Expose val mutedUsers : MutableList<MutedUser> = ArrayList()
)