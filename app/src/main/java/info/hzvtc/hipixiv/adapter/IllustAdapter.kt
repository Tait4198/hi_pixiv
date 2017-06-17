package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.view.SimpleDraweeView
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.IllustResponse

class IllustAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    private lateinit var data : IllustResponse
    private var relPosition = 0

    fun setNewData(data: IllustResponse) {
        typeList.clear()
        relPosition = 0
        this.data = data
        if(data.ranking.isNotEmpty()){
            typeList.add(ITEM_RANKING_TOP)
            relPosition++
            typeList.add(ITEM_RANKING)
            relPosition++
        }
        typeList.add(ITEM_ILLUST_TOP)
        relPosition++
        for(index in 0..data.content.size-1){
            typeList.add(ITEM_ILLUST)
        }
    }

    fun getFull(position: Int) = typeList[position] == ITEM_ILLUST

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when((holder as BindingHolder).type) {
            ITEM_RANKING -> showItemRanking(holder.bind)
            ITEM_ILLUST ->  showItemIllust(holder.bind,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var resId : Int = -1
        when(viewType){
            ITEM_RANKING_TOP -> resId = R.layout.item_ranking_top
            ITEM_RANKING -> resId = R.layout.item_ranking
            ITEM_ILLUST_TOP -> resId = R.layout.item_illust_top
            ITEM_ILLUST -> resId = R.layout.item_illust
        }
        if(resId != -1){
            return BindingHolder(DataBindingUtil.inflate(mLayoutInflater, resId,parent,false),viewType)
        }
        return null
    }

    private fun getRelPosition(position : Int) = position - relPosition

    private fun showItemRanking(bind : ViewDataBinding){
        val root = bind.root
        val recycler : RecyclerView = root.findViewById(R.id.illustRecycler) as RecyclerView
        val adapter = RankingAdapter(context)
        adapter.setNewData(data.ranking)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun showItemIllust(bind : ViewDataBinding,position: Int){
        val root = bind.root
        val cover: SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
        cover.setImageURI(data.content[getRelPosition(position)].imageUrls.square)
    }
}
