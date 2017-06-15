package info.hzvtc.hipixiv.pojo.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorSystem(
        @SerializedName("message") @Expose val message : String,
        @SerializedName("code") @Expose val code : Int
        )
