package info.hzvtc.hipixiv.pojo.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.PixivUser
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.novel.Novel

data class UserPreview(
        @SerializedName("user") @Expose val user : PixivUser,
        @SerializedName("illusts") @Expose val illustList : MutableList<Illust> = ArrayList(),
        @SerializedName("novels") @Expose val novelList : MutableList<Novel> = ArrayList(),
        @SerializedName("is_muted") @Expose val isMuted : Boolean
)
