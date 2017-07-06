package info.hzvtc.hipixiv.adapter.events

import android.view.View
import java.util.*

abstract class CheckDoubleClickListener : View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(v: View?) {
        val current = Calendar.getInstance().timeInMillis
        if(current - lastClickTime > 1000){
            lastClickTime = current
            click(v)
        }
    }

    abstract fun click(v : View?)
}