package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Tag(
        @SerializedName("name") @Expose val name : String
)
