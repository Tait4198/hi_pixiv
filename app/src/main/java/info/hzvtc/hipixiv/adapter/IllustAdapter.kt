package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.view.SimpleDraweeView
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.pojo.illust.IllustResponse

class IllustAdapter(context: Context) : BaseRecyclerViewAdapter(context = context) {

    lateinit var data : IllustResponse

    fun setNewData(data: IllustResponse) {
        this.data = data
        typeList.clear()
        if(!data.ranking.isNotEmpty()) typeList.add(ITEM_RANKING)
        for(index in 0..data.content.size-1){
            typeList.add(ITEM_ILLUST)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if(holder is BindingHolder){
            val root = holder.getBinding().root
            val cover : SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
            cover.setImageURI(data.content[position].imageUrls.square)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val viewHolder : RecyclerView.ViewHolder?
        when(viewType){
            ITEM_RANKING -> viewHolder = null
            ITEM_ILLUST -> viewHolder = BindingHolder(DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_illust,parent,false))
            else -> viewHolder = null
        }
        return viewHolder
    }

    override fun getItemCount(): Int = typeList.size

    override fun getItemViewType(position: Int) = typeList[position]
}
