package info.hzvtc.hipixiv.net

import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("/v1/application-info/android")
    fun getApplicationInfo() : Observable<String>
}