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
import info.hzvtc.hipixiv.pojo.illust.Illust

class RankingAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    private lateinit var ranking : List<Illust>
    private lateinit var itemClick : IllustItemClick
    private lateinit var itemLike : ItemLike

    init {
        setHasStableIds(true)
    }

    fun setNewData(ranking: List<Illust>) {
        typeList.clear()
        this.ranking = ranking
        for(index in 0..ranking.size-1){
            typeList.add(ItemType.ITEM_RANKING_ILLUST)
        }
    }

    fun setItemClick(itemClick: IllustItemClick){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return  BindingHolder<ItemRankingIllustBinding>(DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_ranking_illust,parent,false),ItemType.ITEM_RANKING_ILLUST)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val bind : ItemRankingIllustBinding = (holder as BindingHolder<ViewDataBinding>).bind as ItemRankingIllustBinding
        val illust = ranking[position]
        //View
        bind.rootView.setOnClickListener({
            itemClick.itemClick(illust)
        })
        bind.cover.setImageURI(illust.imageUrls.medium)
        bind.profile.hierarchy.roundingParams = RoundingParams.asCircle()
        bind.profile.setImageURI(illust.user.profile.medium)
        bind.collectButton.isLiked = illust.isBookmarked
        bind.collectButton.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,position,true,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,position,true,likeButton)
            }
        })
        //Bind
        bind.setVariable(BR.rankingIllust,illust)
    }

}
