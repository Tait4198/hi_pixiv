package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.EmptyResponse
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
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

    @GET
    fun getIllustsNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<IllustResponse>

    @FormUrlEncoded
    @POST("/v2/illust/bookmark/add")
    fun postLikeIllust(@Header("Authorization") authorization: String, @Field("illust_id") illustId: Int,
                                @Field("restrict") restrict: String): Observable<EmptyResponse>

    @FormUrlEncoded
    @POST("/v1/illust/bookmark/delete")
    fun postUnlikeIllust(@Header("Authorization") authorization: String,
                                  @Field("illust_id") illustId: Int): Observable<EmptyResponse>
}