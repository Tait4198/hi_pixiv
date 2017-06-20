package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

abstract class BaseRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RANKING_TOP = 0
    val ITEM_RANKING = 1
    val ITEM_RANKING_ILLUST = 2
    val ITEM_ILLUST_TOP = 3
    val ITEM_ILLUST = 4
    val ITEM_MANGA = 5
    val ITEM_PROGRESS = 6

    val mLayoutInflater : LayoutInflater = LayoutInflater.from(context)
    val typeList : MutableList<Int> = ArrayList()

    override fun getItemCount(): Int = typeList.size

    override fun getItemViewType(position: Int) = typeList[position]

    override fun getItemId(position: Int) = position.toLong()
}