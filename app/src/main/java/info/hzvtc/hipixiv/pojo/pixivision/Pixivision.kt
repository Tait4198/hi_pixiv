package info.hzvtc.hipixiv.pojo.pixivision

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pixivision(
        @SerializedName("id") @Expose val id : Int,
        @SerializedName("title") @Expose val title : String,
        @SerializedName("pure_title") @Expose val pureTitle : String,
        @SerializedName("thumbnail") @Expose val thumbnail : String,
        @SerializedName("article_url") @Expose val articleUrl : String,
        @SerializedName("publish_date") @Expose val publishDate : String,
        @SerializedName("category") @Expose val category : String,
        @SerializedName("subcategory_label") @Expose val subcategoryLabel : String
)