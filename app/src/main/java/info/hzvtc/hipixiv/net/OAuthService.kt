package info.hzvtc.hipixiv.net

import info.hzvtc.hipixiv.pojo.token.OAuthToken
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OAuthService{
    @FormUrlEncoded
    @POST("/auth/token")
    fun postAuthToken(@Field("client_id")  clientId : String ,
                      @Field("client_secret") clientSecret : String ,
                      @Field("grant_type") grantType : String ,
                      @Field("username") username : String,
                      @Field("password") password : String,
                      @Field("device_token") deviceToken : String,
                      @Field("get_secure_url") getSecureUrl : Boolean) : Observable<OAuthToken>

    @FormUrlEncoded
    @POST("/auth/token")
    fun postRefreshAuthToken(
            @Field("client_id") clientId : String,
            @Field("client_secret") clientSecret : String,
            @Field("grant_type") grantType : String,
            @Field("refresh_token") refreshToken : String,
            @Field("device_token") deviceToken : String,
            @Field("get_secure_url") getSecureUrl : Boolean) : Observable<OAuthToken>
}
