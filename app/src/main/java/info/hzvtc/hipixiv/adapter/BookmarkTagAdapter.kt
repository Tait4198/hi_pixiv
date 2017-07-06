package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.CheckDoubleClickListener
import info.hzvtc.hipixiv.adapter.events.TagItemClick
import info.hzvtc.hipixiv.databinding.ItemBookmarkTagBinding
import info.hzvtc.hipixiv.databinding.ItemBookmarkTagSelectedBinding
import info.hzvtc.hipixiv.pojo.tag.BookmarkTag
import info.hzvtc.hipixiv.pojo.tag.BookmarkTagResponse


//todo 加载更多?
class BookmarkTagAdapter(context: Context) : BaseRecyclerViewAdapter(context = context) {

    private var tagItemClick : TagItemClick? = null
    private val tags : MutableList<BookmarkTag> = ArrayList()
    private val defaultTagItems = context.resources.getStringArray(R.array.default_tag_name_items)

    init {
        setHasStableIds(true)
    }

    fun initNewData(data : BookmarkTagResponse?,lastPosition : Int){
        notifyItemRangeRemoved(0, typeList.size)
        typeList.clear()
        tags.clear()
        (0..defaultTagItems.size-1).mapTo(tags) { BookmarkTag(defaultTagItems[it],0) }
        if(data != null) tags.addAll(data.tags)
        for(index in 0..tags.size-1){
            if(lastPosition == index){
                typeList.add(ItemType.ITEM_BOOKMARK_TAG_SELECTED)
            }else{
                typeList.add(ItemType.ITEM_BOOKMARK_TAG)
            }
        }
        notifyItemRangeInserted(0, typeList.size)
    }

    fun updateLastPositionItem(lastPosition: Int){
        if(lastPosition >= 0 && lastPosition < typeList.size){
            typeList.removeAt(lastPosition)
            typeList.add(lastPosition,ItemType.ITEM_BOOKMARK_TAG)
            notifyItemRangeChanged(lastPosition,1)
        }
    }

    fun updatePositionItem(position : Int){
        if(position < typeList.size){
            typeList.removeAt(position)
            typeList.add(position,ItemType.ITEM_BOOKMARK_TAG_SELECTED)
            notifyItemRangeChanged(position,1)
        }
    }

    fun setTagItemClick(tagItemClick: TagItemClick){
        this.tagItemClick = tagItemClick
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val bind = (holder as BindingHolder<ViewDataBinding>).bind
        if(bind is ItemBookmarkTagBinding){
            bind.root.setOnClickListener(object : CheckDoubleClickListener() {
                override fun click(v: View?) {
                    tagItemClick?.itemClick(position,tags[position].name)
                }
            })
        }
        bind.setVariable(BR.bookmarkTag,tags[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType) {
            ItemType.ITEM_BOOKMARK_TAG.value -> {
                holder = BindingHolder<ItemBookmarkTagBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_bookmark_tag, parent, false), ItemType.ITEM_BOOKMARK_TAG)
            }
            ItemType.ITEM_BOOKMARK_TAG_SELECTED.value -> {
                holder = BindingHolder<ItemBookmarkTagSelectedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_bookmark_tag_selected, parent, false), ItemType.ITEM_BOOKMARK_TAG_SELECTED)
            }
        }
        return holder
    }
}