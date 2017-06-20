package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.IllustResponse

//isMange = true 漫画/ false 插画
class IllustAdapter(private var context: Context,private var isMange : Boolean) : BaseRecyclerViewAdapter(context = context) {

    var nextUrl = ""

    private lateinit var data : IllustResponse
    private lateinit var itemClick : IllustItemClick
    private lateinit var itemLike : ItemLike

    private var realPosition = 0
    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0
    private var tempProgressIndex = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(data: IllustResponse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
            realPosition = 0
        }
        //Set update index
        positionStart = typeList.size
        moreDataSize = data.content.size
        //NextUrl
        if(!data.nextUrl.isNullOrEmpty()) nextUrl = data.nextUrl
        //New Data
        this.data = data
        //Init typeList
        if(data.ranking.isNotEmpty()){
            typeList.add(ITEM_RANKING_TOP)
            realPosition++
            typeList.add(ITEM_RANKING)
            realPosition++
            typeList.add(ITEM_ILLUST_TOP)
            realPosition++
        }
        for(index in 0..data.content.size-1){
            if(isMange) typeList.add(ITEM_MANGA) else typeList.add(ITEM_ILLUST)
        }
    }

    fun addMoreData(moreData: IllustResponse){
        positionStart = typeList.size + 1
        moreDataSize = moreData.content.size

        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        for(index in 0..moreData.content.size-1){
            if(isMange) typeList.add(ITEM_MANGA) else typeList.add(ITEM_ILLUST)
        }
        data.content.addAll(moreData.content)
    }

    fun updateUI(isNew : Boolean){
        if(isNew) notifyItemRangeRemoved(0, tempTypeListSize)
        notifyItemRangeInserted(positionStart, moreDataSize)
    }

    fun updateBookmarked(position: Int,isBookmarked : Boolean,isRank : Boolean){
        if(position <= data.content.size && !isRank){
            data.content[position].isBookmarked = isBookmarked
        }else if(position <= data.ranking.size && isRank){
            data.ranking[position].isBookmarked = isBookmarked
        }
    }

    fun getFull(position: Int): Boolean {
        return typeList[position] == ITEM_ILLUST || typeList[position] == ITEM_MANGA
    }

    fun setItemClick(itemClick: IllustItemClick){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
    }

    fun setProgress(isShow : Boolean){
        if(isShow){
            typeList.add(ITEM_PROGRESS)
            tempProgressIndex = typeList.size
            notifyItemRangeInserted(tempProgressIndex,1)
        }else{
            typeList.removeAt(tempProgressIndex-1)
            notifyItemRangeRemoved(tempProgressIndex-1,1)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder).type
        //Manga Full
        if(isMange && type != ITEM_MANGA){
            val layoutParams = StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.isFullSpan = true
            holder.itemView.layoutParams = layoutParams
        }
        //Item
        when(type) {
            ITEM_RANKING -> showItemRanking(holder.bind)
            ITEM_ILLUST ->  showItemIllust(holder.bind,position)
            ITEM_MANGA ->  showItemManga(holder.bind,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var resId : Int = -1
        when(viewType){
            ITEM_RANKING_TOP -> resId = R.layout.item_ranking_top
            ITEM_RANKING -> resId = R.layout.item_ranking
            ITEM_ILLUST_TOP -> resId = R.layout.item_illust_top
            ITEM_ILLUST -> resId = R.layout.item_illust
            ITEM_MANGA -> resId = R.layout.item_manga
            ITEM_PROGRESS -> resId = R.layout.item_progress
        }
        if(resId != -1){
            return BindingHolder(DataBindingUtil.inflate(mLayoutInflater, resId,parent,false),viewType)
        }
        return null
    }

    private fun getRealPosition(position : Int) = position - realPosition

    private fun showItemRanking(bind : ViewDataBinding){
        val root = bind.root
        val recycler : RecyclerView = root.findViewById(R.id.illustRecycler) as RecyclerView
        val adapter = RankingAdapter(context)
        adapter.setNewData(data.ranking)
        adapter.setItemClick(itemClick)
        adapter.setItemLike(itemLike)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun showItemIllust(bind : ViewDataBinding,position: Int){
        val illust = data.content[getRealPosition(position)]
        showItemImage(illust,bind,position)
        bind.setVariable(BR.illust,illust)
        bind.setVariable(BR.illustItemClick,itemClick)
    }

    private fun showItemManga(bind : ViewDataBinding,position: Int){
        val illust = data.content[getRealPosition(position)]
        showItemImage(illust,bind,position)
        bind.setVariable(BR.mangaIllust,illust)
        bind.setVariable(BR.mangaItemClick,itemClick)
    }

    private fun showItemImage(illust: Illust,bind : ViewDataBinding,position: Int){
        val root = bind.root
        val cover: SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
        val like : LikeButton = root.findViewById(R.id.collect_button) as LikeButton
        //View
        val coverHeight = if(isMange) 240f else 300f
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    coverHeight, context.resources.displayMetrics).toInt()
            //todo 横屏使用large图片 Set
        }
        cover.setImageURI(illust.imageUrls.medium)
        like.isLiked = illust.isBookmarked
        like.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })
    }
}
