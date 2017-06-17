package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.pojo.illust.Illust

class RankingAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    lateinit var ranking : List<Illust>

    fun setNewData(ranking: List<Illust>) {
        typeList.clear()
        this.ranking = ranking
        for(index in 0..ranking.size-1){
            typeList.add(ITEM_RANKING_ILLUST)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return  BindingHolder(DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_ranking_illust,parent,false),ITEM_RANKING_ILLUST)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val root = (holder as BindingHolder).bind.root
        val cover: SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
        cover.setImageURI(ranking[position].imageUrls.square)
    }

}
