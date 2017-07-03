package info.hzvtc.hipixiv.pojo

import android.os.Parcel
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

data class Suggestion(val suggestion : String,val isHistory : Boolean) : SearchSuggestion{

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        //
    }

    override fun describeContents(): Int = 0

    override fun getBody(): String = suggestion

}