package info.hzvtc.hipixiv.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

class BindingHolder(val bind : ViewDataBinding) : RecyclerView.ViewHolder(bind.root) {
    fun getBinding() = bind
}
