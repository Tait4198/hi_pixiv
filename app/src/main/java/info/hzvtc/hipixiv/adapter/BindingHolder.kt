package info.hzvtc.hipixiv.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView


class BindingHolder<out T : ViewDataBinding>(val bind : T, val type : ItemType) : RecyclerView.ViewHolder(bind.root)
