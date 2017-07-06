package info.hzvtc.hipixiv.pojo.illust

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.*

data class Illust(
        @SerializedName("id") @Expose val pixivId : Int,
        @SerializedName("title") @Expose val title : String,
        @SerializedName("type") @Expose val type : String,
        @SerializedName("image_urls") @Expose val imageUrls : ImageUrls,
        @SerializedName("caption") @Expose val caption : String,
        @SerializedName("restrict") @Expose val restrict : Int,
        @SerializedName("user") @Expose val user : PixivUser,
        @SerializedName("tags") @Expose val tags : List<Tag> = ArrayList<Tag>(),
        @SerializedName("tools") @Expose val tools : List<String> = ArrayList<String>(),
        @SerializedName("create_date") @Expose val createDate : String,
        @SerializedName("page_count") @Expose val pageCount : Int,
        @SerializedName("width") @Expose val width : Int,
        @SerializedName("height") @Expose val height : Int,
        @SerializedName("sanity_level") @Expose val sanityLevel : Int,
        @SerializedName("meta_single_page") @Expose val metaSinglePage : MetaSinglePage,
        @SerializedName("meta_pages") @Expose val metaPages : List<MetaImageUrls> = ArrayList<MetaImageUrls>(),
        @SerializedName("total_view") @Expose val view : Int,
        @SerializedName("total_bookmarks") @Expose val bookmarks : Int,
        @SerializedName("is_bookmarked") @Expose var isBookmarked : Boolean,
        @SerializedName("visible") @Expose val visible : Boolean,
        @SerializedName("is_muted") @Expose val isMuted : Boolean
)
