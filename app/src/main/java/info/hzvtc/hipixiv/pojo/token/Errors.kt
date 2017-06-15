package info.hzvtc.hipixiv.pojo.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Errors(
        @SerializedName("system") @Expose val errorSystem: ErrorSystem
        )