package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("/v1/illust/recommended?filter=for_android")
    fun getRecommendedIllusts(@Header("Authorization") authorization : String,
                              @Query("include_ranking_illusts") bool : Boolean) : Observable<IllustResponse>

    @GET
    fun getIllustsNext(@Header("Authorization") authorization : String,
                       @Url url : String) : Observable<IllustResponse>
}