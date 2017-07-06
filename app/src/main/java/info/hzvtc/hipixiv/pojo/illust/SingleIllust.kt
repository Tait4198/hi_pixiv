package info.hzvtc.hipixiv.pojo.illust

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SingleIllust(
        @SerializedName("illust") @Expose val illust : Illust?
)
