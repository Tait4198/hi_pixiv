package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ItemPixivisionBinding
import info.hzvtc.hipixiv.databinding.ItemProgressBinding
import info.hzvtc.hipixiv.pojo.pixivision.PixivisionResopnse

class PixivisionAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    var nextUrl : String? = ""

    private lateinit var data : PixivisionResopnse
    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0
    private var tempProgressIndex = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(newData: PixivisionResopnse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
        }
        //Set update index
        positionStart = typeList.size
        moreDataSize = 0
        //NextUrl
        if(!newData.nextUrl.isNullOrEmpty()) nextUrl = newData.nextUrl else nextUrl = ""
        //New Data
        for(index in newData.content.indices){
            moreDataSize++
            typeList.add(ItemType.ITEM_PIXIVISION)
        }
        this.data = newData
    }

    fun addMoreData(moreData: PixivisionResopnse){
        positionStart = typeList.size + 1
        moreDataSize = 0
        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        for(index in moreData.content.indices){
            moreDataSize++
            typeList.add(ItemType.ITEM_PIXIVISION)
        }
        data.content.addAll(moreData.content)
    }

    fun updateUI(isNew : Boolean){
        if(isNew) notifyItemRangeRemoved(0, tempTypeListSize)
        notifyItemRangeInserted(positionStart, moreDataSize)
    }

    fun setProgress(isShow : Boolean){
        if(isShow){
            typeList.add(ItemType.ITEM_PROGRESS)
            tempProgressIndex = typeList.size
            notifyItemRangeInserted(tempProgressIndex,1)
        }else{
            typeList.removeAt(tempProgressIndex-1)
            notifyItemRangeRemoved(tempProgressIndex-1,1)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        if(type == ItemType.ITEM_PIXIVISION){
            val mBind : ItemPixivisionBinding = holder.bind as ItemPixivisionBinding
            val pixivison = data.content[position]
            mBind.spotlightCover.setImageURI(pixivison.thumbnail)
            mBind.spotlightTitle.text = pixivison.title
            var color = ContextCompat.getColor(context,R.color.md_blue_500)
            when(pixivison.category){
                "tutorial" -> color = ContextCompat.getColor(context,R.color.md_green_500)
                "inspiration" -> color = ContextCompat.getColor(context,R.color.md_deep_orange_500)
            }
            mBind.label.setBackgroundColor(color)
            mBind.label.text = pixivison.subcategoryLabel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType){
            ItemType.ITEM_PIXIVISION.value ->{
                holder = BindingHolder<ItemPixivisionBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_pixivision,parent,false),ItemType.ITEM_PIXIVISION)
            }
            ItemType.ITEM_PROGRESS.value -> {
                holder = BindingHolder<ItemProgressBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_progress,parent,false),ItemType.ITEM_PROGRESS)
            }
        }
        return holder
    }

}
