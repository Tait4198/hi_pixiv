package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.AutoCompleteResponse
import info.hzvtc.hipixiv.pojo.EmptyResponse
import info.hzvtc.hipixiv.pojo.comment.CommentResponse
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.pojo.illust.SingleIllust
import info.hzvtc.hipixiv.pojo.mute.MuteResponse
import info.hzvtc.hipixiv.pojo.pixivision.PixivisionResopnse
import info.hzvtc.hipixiv.pojo.tag.BookmarkTagResponse
import info.hzvtc.hipixiv.pojo.trend.TrendTagsResponse
import info.hzvtc.hipixiv.pojo.user.UserPreview
import info.hzvtc.hipixiv.pojo.user.UserResponse
import io.reactivex.Observable
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded

interface ApiService {
    @GET("/v1/illust/recommended?filter=for_android")
    fun getRecommendedIllusts(@Header("Authorization") authorization : String,
                              @Query("include_ranking_illusts") bool : Boolean) : Observable<IllustResponse>

    @GET("/v1/manga/recommended?filter=for_android")
    fun getRecommendedMangaList(@Header("Authorization") authorization : String,
                              @Query("include_ranking_illusts") bool : Boolean) : Observable<IllustResponse>

    @GET("/v2/illust/follow")
    fun getFollowIllusts(@Header("Authorization") authorization: String,
                         @Query("restrict") restrict: String): Observable<IllustResponse>

    @GET("/v1/user/recommended?filter=for_android")
    fun getUserRecommended(@Header("Authorization") authorization: String): Observable<UserResponse>

    @GET("/v1/illust/new?filter=for_android")
    fun getNewIllust(@Header("Authorization") authorization: String,
                              @Query("content_type") type: String): Observable<IllustResponse>

    @GET("/v2/illust/mypixiv")
    fun getMyPixivIllusts(@Header("Authorization") abstract : String): Observable<IllustResponse>

    @GET("v1/user/bookmark-tags/illust")
    fun getIllustBookmarkTags(@Header("Authorization") authorization: String, @Query("user_id") userId: Int,
                                       @Query("restrict") restrict: String): Observable<BookmarkTagResponse>

    @GET("/v1/user/bookmarks/illust")
    fun getLikeIllust(@Header("Authorization") authorization: String, @Query("user_id") userId : Int,
                      @Query("restrict") restrict : String) : Observable<IllustResponse>

    @GET("/v1/user/bookmarks/illust")
    fun getLikeIllust(@Header("Authorization") authorization: String, @Query("user_id") user_id : Int,
                      @Query("restrict") restrict : String, @Query("tag") tag : String) : Observable<IllustResponse>

    @GET("/v1/user/browsing-history/illusts")
    fun getIllustBrowsingHistory(@Header("Authorization") authorization: String): Observable<IllustResponse>

    @GET("/v1/user/following?filter=for_android")
    fun getUserFollowing(@Header("Authorization") authorization: String, @Query("user_id") userId: Int,
                         @Query("restrict") restrict: String): Observable<UserResponse>

    @GET("/v1/user/follower?filter=for_android")
    fun getUserFollower(@Header("Authorization") authorization: String,
                        @Query("user_id") userId: Int): Observable<UserResponse>

    @GET("/v1/user/mypixiv?filter=for_android")
    fun getUserMyPixiv(@Header("Authorization") authorization: String,
                       @Query("user_id") userId: Int): Observable<UserResponse>

    @GET("/v1/illust/ranking?filter=for_android")
    fun getIllustRanking(@Header("Authorization") authorization: String,
                         @Query("mode") mode: String): Observable<IllustResponse>

    @GET("/v1/illust/ranking?filter=for_android")
    fun getIllustRanking(@Header("Authorization") authorization: String, @Query("mode") mode: String
                         , @Query("date") date: String): Observable<IllustResponse>

    @GET("/v1/spotlight/articles?filter=for_android")
    fun getPixivisionArticles(@Header("Authorization") authorization: String,
                              @Query("category") category: String): Observable<PixivisionResopnse>

    @GET("/v1/search/autocomplete")
    fun getSearchAutoCompleteKeywords(@Header("Authorization") authorization: String,
                                      @Query("word") word: String): Observable<AutoCompleteResponse>

    @GET("/v1/trending-tags/illust?filter=for_android")
    fun getIllustTrendTags(@Header("Authorization") authorization: String): Observable<TrendTagsResponse>

    //@Query("bookmark_num") bookmarkNum: Int?
    @GET("/v1/search/illust?filter=for_android")
    fun getSearchIllust(@Header("Authorization") authorization: String,@Query("word") word: String,
                        @Query("sort") sort: String, @Query("search_target") searchTarget: String,
                        @Query("duration") duration: String?): Observable<IllustResponse>

    @GET("/v1/search/user?filter=for_android")
    fun getSearchUser(@Header("Authorization") authorization : String, @Query("word") word : String): Observable<UserResponse>

    @GET("/v1/illust/detail?filter=for_android")
    fun getIllust(@Header("Authorization") authorization: String, @Query("illust_id") illustId: Int): Observable<SingleIllust>

    @GET("/v1/user/detail?filter=for_android")
    fun getUser(@Header("Authorization") authorization: String, @Query("user_id") userId: Int): Observable<EmptyResponse>

    @GET("/v1/user/illusts?filter=for_android")
    fun getUserIllusts(@Header("Authorization") authorization: String, @Query("user_id") userId: Int,
                       @Query("type") type: String): Observable<EmptyResponse>

    @GET("/v1/illust/comments")
    fun getIllustComments(@Header("Authorization") authorization: String, @Query("illust_id") illustId: Int): Observable<CommentResponse>

    @GET("/v2/illust/related?filter=for_android")
    fun getIllustRecommended(@Header("Authorization") authorization: String, @Query("illust_id") illustId: Int): Observable<IllustResponse>

    @GET
    fun getIllustNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<IllustResponse>

    @GET
    fun getUserNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<UserResponse>

    @GET
    fun getPixivisionNext(@Header("Authorization") authorization : String,
                    @Url url : String) : Observable<PixivisionResopnse>

    @GET
    fun getCommentsNext(@Header("Authorization") authorization : String,
                          @Url url : String) : Observable<CommentResponse>

    @GET("/v1/mute/list")
    fun getMutedList(@Header("Authorization") authorization: String): Observable<MuteResponse>

    @FormUrlEncoded
    @POST("/v1/mute/edit")
    fun postMuteSetting(@Header("Authorization") authorization : String,
                        @Field("add_user_ids[]") addUserList : List<Int>,
                        @Field("delete_user_ids[]") delUserList : List<Long>,
                        @Field("add_tags[]") addTagList : List<Long>,
                        @Field("delete_tags[]") delTagList : List<Long>) : Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v2/illust/bookmark/add")
    fun postLikeIllust(@Header("Authorization") authorization: String, @Field("illust_id") illustId: Int,
                                @Field("restrict") restrict: String): Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v1/illust/bookmark/delete")
    fun postUnlikeIllust(@Header("Authorization") authorization: String,
                                  @Field("illust_id") illustId: Int): Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v1/user/follow/add")
    fun postFollowUser(@Header("Authorization") authorization: String, @Field("user_id") userId: Int,
                       @Field("restrict") restrict: String): Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v1/user/follow/delete")
    fun postUnfollowUser(@Header("Authorization") authorization: String,
                                  @Field("user_id") userId: Int): Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v2/user/browsing-history/illust/add")
    fun postAddIllustBrowsingHistory(@Header("Authorization") authorization: String,
                                     @Field("illust_ids[]") list: List<Int>): Observable<EmptyResponse>
}