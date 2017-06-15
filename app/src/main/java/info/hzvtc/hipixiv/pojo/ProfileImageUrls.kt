package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileImageUrls(
        @SerializedName("px_16x16") @Expose val square : String,
        @SerializedName("px_50x50") @Expose val medium : String,
        @SerializedName("px_170x170") @Expose val large : String
        )