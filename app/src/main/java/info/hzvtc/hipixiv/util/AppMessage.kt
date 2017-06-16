package info.hzvtc.hipixiv.util

import android.content.Context
import android.util.Log
import android.widget.Toast

class AppMessage {
    companion object{

        fun toastMessageLong(resId : Int,context: Context){
            Toast.makeText(context,context.getString(resId),Toast.LENGTH_LONG).show()
        }

        fun toastMessageShort(resId : Int,context: Context){
            Toast.makeText(context,context.getString(resId),Toast.LENGTH_SHORT).show()
        }

        fun toastMessageLong(msg : String,context: Context){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }

        fun toastMessageShort(msg : String,context: Context){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
        }
    }
}