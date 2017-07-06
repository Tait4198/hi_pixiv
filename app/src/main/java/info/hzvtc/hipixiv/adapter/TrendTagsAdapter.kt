package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.CheckDoubleClickListener
import info.hzvtc.hipixiv.adapter.events.TrendTagItemClick
import info.hzvtc.hipixiv.databinding.ItemTrendTagBinding
import info.hzvtc.hipixiv.databinding.ItemTrendTagTopBinding
import info.hzvtc.hipixiv.pojo.trend.TrendTagsResponse

class TrendTagsAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    private lateinit var data : TrendTagsResponse
    private var trendTagItemClick : TrendTagItemClick? = null

    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(newData : TrendTagsResponse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
        }
        //Set update index
        positionStart = typeList.size
        moreDataSize = 0
        //New Data
        for(index in newData.trendTags.indices){
            moreDataSize++
            if(index == 0)
                typeList.add(ItemType.ITEM_TREND_TAG_TOP)
            else
                typeList.add(ItemType.ITEM_TREND_TAG)
        }
        this.data = newData
    }

    fun getFull(position: Int): Boolean {
        return typeList[position] == ItemType.ITEM_TREND_TAG
    }

    fun updateUI(isNew : Boolean){
        if(isNew) notifyItemRangeRemoved(0, tempTypeListSize)
        notifyItemRangeInserted(positionStart, moreDataSize)
    }

    fun setTrendTagItemClick(trendTagItemClick: TrendTagItemClick){
        this.trendTagItemClick = trendTagItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType){
            ItemType.ITEM_TREND_TAG.value ->{
                holder = BindingHolder<ItemTrendTagBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_trend_tag,parent,false),ItemType.ITEM_TREND_TAG)
            }
            ItemType.ITEM_TREND_TAG_TOP.value ->{
                holder = BindingHolder<ItemTrendTagTopBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_trend_tag_top,parent,false),ItemType.ITEM_TREND_TAG_TOP)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val bind = (holder as BindingHolder<ViewDataBinding>).bind
        val trendTag = data.trendTags[position]
        if(bind is ItemTrendTagTopBinding){
            bind.trendCover.setImageURI(trendTag.illust.imageUrls.large)
            bind.trendTagTitle.text = trendTag.tag
        }
        if(bind is ItemTrendTagBinding){
            bind.trendCover.setImageURI(trendTag.illust.imageUrls.medium)
            bind.trendTagTitle.text = trendTag.tag
        }
        bind.root.setOnClickListener(object : CheckDoubleClickListener(){
            override fun click(v: View?) {
                trendTagItemClick?.itemClick(trendTag.tag)
            }
        })
    }

}