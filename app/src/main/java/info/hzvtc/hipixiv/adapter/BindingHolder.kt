package info.hzvtc.hipixiv.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView


class BindingHolder(val bind : ViewDataBinding, val type : Int) : RecyclerView.ViewHolder(bind.root)
