package info.hzvtc.hipixiv.pojo.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OAuthToken(
        @SerializedName("has_error") @Expose val hasError : Boolean = false,
        @SerializedName("response") @Expose val oAuthResponse: OAuthResponse,
        @SerializedName("errors") @Expose val errors: Errors?
)