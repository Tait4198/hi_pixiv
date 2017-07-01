package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.facebook.drawee.generic.RoundingParams
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ItemRankingIllustBinding
import info.hzvtc.hipixiv.databinding.ItemRankingMutedBinding
import info.hzvtc.hipixiv.pojo.illust.Illust

class RankingAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

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
            if(!ranking[index-jump].isMuted){
                typeList.add(ItemType.ITEM_RANKING_ILLUST)
            }else{
                ranking.removeAt(index-jump)
                jump++
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
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if((holder as BindingHolder<ViewDataBinding>).bind is ItemRankingIllustBinding){
            val bind : ItemRankingIllustBinding = holder.bind as ItemRankingIllustBinding
            val illust = ranking[position]
            //View
            bind.rootView.setOnClickListener({
                itemClick?.itemClick(illust)
            })
            bind.cover.setImageURI(illust.imageUrls.medium)
            bind.profile.hierarchy.roundingParams = RoundingParams.asCircle()
            bind.profile.setImageURI(illust.user.profile.medium)
            bind.collectButton.isLiked = illust.isBookmarked
            bind.collectButton.setOnLikeListener(object : OnLikeListener{
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
    }

}
