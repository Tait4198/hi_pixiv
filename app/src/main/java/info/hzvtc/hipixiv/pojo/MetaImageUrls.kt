package info.hzvtc.hipixiv.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class MetaImageUrls(@SerializedName("image_urls") @Expose val imageUrls : ImageUrls)