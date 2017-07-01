package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.EmptyResponse
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.pojo.tag.BookmarkTagResponse
import info.hzvtc.hipixiv.pojo.user.UserResponse
import io.reactivex.Observable
import retrofit2.http.*

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

    @GET
    fun getIllustNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<IllustResponse>

    @GET
    fun getUserNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<UserResponse>

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
}