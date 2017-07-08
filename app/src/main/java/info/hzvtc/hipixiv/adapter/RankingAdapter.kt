package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.generic.RoundingParams
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.CheckDoubleClickListener
import info.hzvtc.hipixiv.adapter.events.ItemClick
import info.hzvtc.hipixiv.adapter.events.ItemLike
import info.hzvtc.hipixiv.databinding.*
import info.hzvtc.hipixiv.pojo.illust.Illust

class RankingAdapter(val context: Context,val type : Type) : BaseRecyclerViewAdapter(context = context) {

    private lateinit var ranking : MutableList<Illust>
    private var itemClick : ItemClick? = null
    private var itemLike : ItemLike?  = null

    init {
        setHasStableIds(true)
    }

    fun setNewData(ranking: MutableList<Illust>) {
        typeList.clear()
        val max = ranking.size-1
        var jump = 0
        for(index in 0..max){
            when(type){
                Type.RANKING->{
                    if(!ranking[index-jump].isMuted){
                        typeList.add(ItemType.ITEM_RANKING_ILLUST)
                    }else{
                        ranking.removeAt(index-jump)
                        jump++
                    }
                }
                Type.RELATED->{
                    if(!ranking[index-jump].isMuted){
                        typeList.add(ItemType.ITEM_RELATED_ILLUST)
                    }else{
                        ranking.removeAt(index-jump)
                        jump++
                    }
                }
            }
        }
        this.ranking = ranking
    }

    fun setItemClick(itemClick: ItemClick?){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike?){
        this.itemLike = itemLike
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType){
            ItemType.ITEM_RANKING_ILLUST.value ->{
                holder = BindingHolder<ItemRankingIllustBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_ranking_illust,parent,false),ItemType.ITEM_RANKING_ILLUST)
            }
            ItemType.ITEM_RANKING_MUTED.value->{
                holder = BindingHolder<ItemRankingMutedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_ranking_muted,parent,false),ItemType.ITEM_RANKING_MUTED)
            }
            ItemType.ITEM_RELATED_ILLUST.value ->{
                holder = BindingHolder<ItemRelatedIllustBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_related_illust,parent,false),ItemType.ITEM_RELATED_ILLUST)
            }
            ItemType.ITEM_ILLUST_MUTED.value -> {
                holder = BindingHolder<ItemIllustMutedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_illust_muted,parent,false),ItemType.ITEM_ILLUST_MUTED)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        when(type) {
            ItemType.ITEM_RANKING_ILLUST -> showItemRankingIllust(holder.bind,position)
            ItemType.ITEM_RELATED_ILLUST ->  showItemIllust(holder.bind,position)
            else -> {
                //
            }
        }
    }

    private fun showItemIllust(bind: ViewDataBinding, position: Int) {
        val mBind : ItemRelatedIllustBinding = bind as ItemRelatedIllustBinding
        val illust = ranking[position]
        mBind.rootView.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                itemClick?.itemClick(illust)
            }
        })
        mBind.cover.setImageURI(illust.imageUrls.medium)
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    300f , context.resources.displayMetrics).toInt()
        }
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike?.like(illust.pixivId,position,false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike?.unlike(illust.pixivId,position,false,likeButton)
            }
        })
        //bind
        bind.setVariable(BR.illustPageCount,illust.metaPages.size)
    }

    private fun showItemRankingIllust(bind: ViewDataBinding, position: Int) {
        val mBind : ItemRankingIllustBinding = bind as ItemRankingIllustBinding
        val illust = ranking[position]
        //View
        mBind.rootView.setOnClickListener(object : CheckDoubleClickListener(){
            override fun click(v: View?) {
                itemClick?.itemClick(illust)
            }
        })
        mBind.cover.setImageURI(illust.imageUrls.medium)
        mBind.profile.hierarchy.roundingParams = RoundingParams.asCircle()
        mBind.profile.setImageURI(illust.user.profile.medium)
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                itemLike?.like(illust.pixivId,position,true,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike?.unlike(illust.pixivId,position,true,likeButton)
            }
        })
        //Bind
        bind.setVariable(BR.rankingIllust,illust)
    }


    enum class Type(val value : Int){
        RANKING(0),
        RELATED(1)
    }
}
