package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.NullResponse
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    @GET("/v1/illust/recommended?filter=for_android")
    fun getRecommendedIllusts(@Header("Authorization") authorization : String,
                              @Query("include_ranking_illusts") bool : Boolean) : Observable<IllustResponse>

    @GET
    fun getIllustsNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<IllustResponse>

    @FormUrlEncoded
    @POST("/v2/illust/bookmark/add")
    fun postLikeIllust(@Header("Authorization") authorization: String, @Field("illust_id") illustId: Int,
                                @Field("restrict") restrict: String): Observable<NullResponse>

    @FormUrlEncoded
    @POST("/v1/illust/bookmark/delete")
    fun postUnlikeIllust(@Header("Authorization") authorization: String,
                                  @Field("illust_id") illustId: Int): Observable<NullResponse>
}