package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImageUrls(
        @SerializedName("square_medium") @Expose val square : String,
        @SerializedName("medium") @Expose val medium : String,
        @SerializedName("large") @Expose val large : String,
        @SerializedName("original") @Expose val original : String
)
