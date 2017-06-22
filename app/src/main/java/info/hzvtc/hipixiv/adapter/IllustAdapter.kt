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
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.*
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
            typeList.add(ItemType.ITEM_RANKING_TOP)
            realPosition++
            typeList.add(ItemType.ITEM_RANKING)
            realPosition++
            typeList.add(ItemType.ITEM_ILLUST_TOP)
            realPosition++
        }
        for(index in 0..data.content.size-1){
            if(isMange) typeList.add(ItemType.ITEM_MANGA) else typeList.add(ItemType.ITEM_ILLUST)
        }
    }

    fun addMoreData(moreData: IllustResponse){
        positionStart = typeList.size + 1
        moreDataSize = moreData.content.size

        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        for(index in 0..moreData.content.size-1){
            if(isMange) typeList.add(ItemType.ITEM_MANGA) else typeList.add(ItemType.ITEM_ILLUST)
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
        return typeList[position] == ItemType.ITEM_ILLUST || typeList[position] == ItemType.ITEM_MANGA
    }

    fun setItemClick(itemClick: IllustItemClick){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
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
        //Manga Full
        if(isMange && type != ItemType.ITEM_MANGA){
            val layoutParams = StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.isFullSpan = true
            holder.itemView.layoutParams = layoutParams
        }
        //Item
        when(type) {
            ItemType.ITEM_RANKING -> showItemRanking(holder.bind )
            ItemType.ITEM_ILLUST ->  showItemIllust(holder.bind,position)
            ItemType.ITEM_MANGA ->  showItemManga(holder.bind,position)
            else -> {
                //
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType){
            ItemType.ITEM_RANKING_TOP.value -> {
                holder = BindingHolder<ItemRankingTopBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_ranking_top,parent,false),ItemType.ITEM_RANKING_TOP)
            }
            ItemType.ITEM_RANKING.value -> {
                holder = BindingHolder<ItemRankingBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_ranking,parent,false),ItemType.ITEM_RANKING)
            }
            ItemType.ITEM_ILLUST_TOP.value -> {
                holder = BindingHolder<ItemIllustTopBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_illust_top,parent,false),ItemType.ITEM_ILLUST_TOP)
            }
            ItemType.ITEM_ILLUST.value ->{
                holder = BindingHolder<ItemIllustBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_illust,parent,false),ItemType.ITEM_ILLUST)
            }
            ItemType.ITEM_MANGA.value -> {
                holder = BindingHolder<ItemMangaBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_manga,parent,false),ItemType.ITEM_MANGA)
            }
            ItemType.ITEM_PROGRESS.value -> {
                holder = BindingHolder<ItemProgressBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_progress,parent,false),ItemType.ITEM_PROGRESS)
            }
        }
        return holder
    }

    private fun getRealPosition(position : Int) = position - realPosition

    private fun showItemRanking(bind : ViewDataBinding){
        val mBind : ItemRankingBinding = bind as ItemRankingBinding
        val adapter = RankingAdapter(context)
        adapter.setNewData(data.ranking)
        adapter.setItemClick(itemClick)
        adapter.setItemLike(itemLike)
        mBind.illustRecycler.adapter = adapter
        mBind.illustRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun showItemIllust(bind : ViewDataBinding,position: Int){
        val mBind : ItemIllustBinding = bind as ItemIllustBinding
        val illust = data.content[getRealPosition(position)]
        mBind.rootView.setOnClickListener({ itemClick.itemClick(illust) })
        mBind.cover.setImageURI(illust.imageUrls.medium)
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    300f , context.resources.displayMetrics).toInt()
        }
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })
        //bind
        bind.setVariable(BR.illustPageCount,illust.pageCount)
    }

    private fun showItemManga(bind : ViewDataBinding,position: Int){
        val mBind : ItemMangaBinding = bind as ItemMangaBinding
        val illust = data.content[getRealPosition(position)]
        mBind.rootView.setOnClickListener({ itemClick.itemClick(illust) })
        mBind.cover.setImageURI(illust.imageUrls.medium)
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    240f, context.resources.displayMetrics).toInt()
        }
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }
            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })
        bind.setVariable(BR.mangaIllust,illust)
    }
}
