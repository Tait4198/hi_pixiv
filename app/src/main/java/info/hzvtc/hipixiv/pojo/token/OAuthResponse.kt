package info.hzvtc.hipixiv.pojo.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import info.hzvtc.hipixiv.pojo.User

data class OAuthResponse(
        @SerializedName("access_token") @Expose val accessToken : String,
        @SerializedName("expires_in") @Expose val expiresIn : Int,
        @SerializedName("token_type") @Expose val tokenType : String,
        @SerializedName("scope") @Expose val scope : String,
        @SerializedName("refresh_token") @Expose val refreshToken : String,
        @SerializedName("user") @Expose val user : User,
        @SerializedName("device_token") @Expose val deviceToken : String
)