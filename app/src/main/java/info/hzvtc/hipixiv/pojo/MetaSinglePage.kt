package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MetaSinglePage(
        @SerializedName("original_image_url") @Expose val original : String
)
