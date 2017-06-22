package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

abstract class BaseRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    val mLayoutInflater : LayoutInflater = LayoutInflater.from(context)
    val typeList : MutableList<ItemType> = ArrayList()

    override fun getItemCount(): Int = typeList.size

    override fun getItemViewType(position: Int) = typeList[position].value

    override fun getItemId(position: Int) = position.toLong()
}