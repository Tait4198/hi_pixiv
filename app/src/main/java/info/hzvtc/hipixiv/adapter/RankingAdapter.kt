package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
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
            typeList.add(ITEM_RANKING_ILLUST)
        }
    }

    fun setItemClick(itemClick: IllustItemClick){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return  BindingHolder(DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_ranking_illust,parent,false),ITEM_RANKING_ILLUST)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val bind = (holder as BindingHolder).bind
        val root = bind.root
        val illust = ranking[position]
        val cover: SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
        val profile : SimpleDraweeView = root.findViewById(R.id.profile) as SimpleDraweeView
        val like : LikeButton = root.findViewById(R.id.collect_button) as LikeButton
        //View
        cover.setImageURI(illust.imageUrls.medium)
        profile.hierarchy.roundingParams = RoundingParams.asCircle()
        profile.setImageURI(illust.user.profile.medium)
        like.isLiked = illust.isBookmarked
        like.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,likeButton)
            }
        })
        //Bind
        bind.setVariable(BR.rankingIllust,illust)
        bind.setVariable(BR.rankingPageCountValue,context.getString(R.string.icon_page) + illust.pageCount)
        bind.setVariable(BR.rankingIllustItemClick,itemClick)
    }

}
