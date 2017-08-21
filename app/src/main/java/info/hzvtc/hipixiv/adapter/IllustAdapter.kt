package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.generic.RoundingParams
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.*
import info.hzvtc.hipixiv.databinding.*
import info.hzvtc.hipixiv.pojo.illust.IllustResponse

//todo 屏蔽设定 (长按屏蔽)
class IllustAdapter(val context: Context,val contentType : Type) : BaseRecyclerViewAdapter(context = context) {

    var nextUrl : String? = ""

    private lateinit var data : IllustResponse
    private var itemClick : ItemClick? = null
    private var itemLongClick : ItemLongClick? = null
    private var itemLike : ItemLike? = null
    private var rankingTopClick : RankingTopClick? = null
    private var frontPosition = 0
    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0
    private var tempProgressIndex = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(newData: IllustResponse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
            frontPosition = 0
        }
        //Set update index
        positionStart = typeList.size
        moreDataSize = 0
        //NextUrl
        if(!newData.nextUrl.isNullOrEmpty()) nextUrl = newData.nextUrl else nextUrl = ""
        //New Data
        //Init typeList
        if(newData.ranking.isNotEmpty() && contentType != Type.RANK){
            typeList.add(ItemType.ITEM_RANKING_TOP)
            frontPosition++
            typeList.add(ItemType.ITEM_RANKING)
            frontPosition++
            typeList.add(ItemType.ITEM_ILLUST_TOP)
            frontPosition++
        }
        val max = newData.content.size-1
        var jump = 0
        for(index in 0..max){
            if(!newData.content[index-jump].isMuted){
                moreDataSize++
                if(contentType == Type.RANK){
                    if(index <= 2) typeList.add(ItemType.ITEM_LIST_RANKING_ILLUST) else typeList.add(ItemType.ITEM_ILLUST)
                }else{
                    if(contentType == Type.MANGA) typeList.add(ItemType.ITEM_MANGA) else typeList.add(ItemType.ITEM_ILLUST)
                }
            }else{
                newData.content.removeAt(index-jump)
                jump++
            }
        }
        if(typeList.size == 0){
            moreDataSize++
            typeList.add(ItemType.ITEM_NO_DATA)
        }
        this.data = newData
    }

    fun addMoreData(moreData: IllustResponse){
        positionStart = typeList.size + 1
        moreDataSize = 0
        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        val max = moreData.content.size-1
        var jump = 0
        for(index in 0..max){
            if(!moreData.content[index-jump].isMuted){
                moreDataSize++
                if(contentType == Type.RANK){
                    typeList.add(ItemType.ITEM_ILLUST)
                }else{
                    if(contentType == Type.MANGA) typeList.add(ItemType.ITEM_MANGA) else typeList.add(ItemType.ITEM_ILLUST)
                }
            }else{
                moreData.content.removeAt(index-jump)
                jump++
            }
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
                || typeList[position] == ItemType.ITEM_ILLUST_MUTED
                || typeList[position] == ItemType.ITEM_MANGA_MUTED
    }

    fun setItemClick(itemClick: ItemClick){
        this.itemClick = itemClick
    }

    fun setItemLongClick(itemLongClick: ItemLongClick){
        this.itemLongClick = itemLongClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
    }

    fun setRankingTopClick(rankingTopClick: RankingTopClick){
        this.rankingTopClick = rankingTopClick
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

    fun loadError(){
        if(typeList.size == 0){
            moreDataSize++
            typeList.add(ItemType.ITEM_LOAD_ERROR)
            updateUI(true)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        //Manga Full
        if(contentType == Type.MANGA && type != ItemType.ITEM_MANGA && type != ItemType.ITEM_MANGA_MUTED){
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
            ItemType.ITEM_RANKING_TOP -> showItemRankingTop(holder.bind)
            ItemType.ITEM_LIST_RANKING_ILLUST -> showItemListRankingIllust(holder.bind,position)
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
            ItemType.ITEM_ILLUST_MUTED.value -> {
                holder = BindingHolder<ItemIllustMutedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_illust_muted,parent,false),ItemType.ITEM_ILLUST_MUTED)
            }
            ItemType.ITEM_MANGA_MUTED.value -> {
                holder = BindingHolder<ItemMangaMutedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_manga_muted,parent,false),ItemType.ITEM_MANGA_MUTED)
            }
            ItemType.ITEM_LIST_RANKING_ILLUST.value ->{
                holder = BindingHolder<ItemListRankingIllustBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_list_ranking_illust,parent,false),ItemType.ITEM_LIST_RANKING_ILLUST)
            }
            ItemType.ITEM_NO_DATA.value ->{
                holder = BindingHolder<ItemNoDataBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_no_data, parent, false), ItemType.ITEM_NO_DATA)
            }
            ItemType.ITEM_LOAD_ERROR.value ->{
                holder = BindingHolder<ItemLoadErrorBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_load_error, parent, false), ItemType.ITEM_LOAD_ERROR)
            }
        }
        return holder
    }

    private fun getRealPosition(position : Int) = position - frontPosition

    private fun  showItemRankingTop(bind: ViewDataBinding) {
        val mBind : ItemRankingTopBinding = bind as ItemRankingTopBinding
        val type = if(contentType == Type.MANGA) RankingType.MANGA else RankingType.ILLUST
        val rankClick = object : CheckDoubleClickListener(){
            override fun click(v: View?) {
                rankingTopClick?.itemClick(type)
            }
        }
        mBind.root.setOnClickListener(rankClick)
        mBind.seeMore.setOnClickListener(rankClick)
    }

    private fun showItemRanking(bind : ViewDataBinding){
        val mBind : ItemRankingBinding = bind as ItemRankingBinding
        val adapter = RankingAdapter(context,RankingAdapter.Type.RANKING)
        adapter.setNewData(data.ranking)
        adapter.setItemClick(itemClick)
        adapter.setItemLike(itemLike)
        mBind.illustRecycler.adapter = adapter
        mBind.illustRecycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    fun showItemListRankingIllust(bind: ViewDataBinding,position: Int){
        val mBind : ItemListRankingIllustBinding = bind as ItemListRankingIllustBinding
        val illust = data.content[getRealPosition(position)]

        mBind.cover.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                itemClick?.itemClick(illust)
            }
        })

        mBind.cover.setImageURI(illust.imageUrls.large)
        val roundingParams = RoundingParams()
        roundingParams.setBorder(ContextCompat.getColor(context,R.color.colorTextSecond), 2f)
        roundingParams.roundAsCircle = true
        mBind.userProfile.hierarchy.roundingParams = roundingParams
        mBind.userProfile.setImageURI(illust.user.profile.medium)

        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    320f , context.resources.displayMetrics).toInt()
        }

        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike?.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike?.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })

        bind.setVariable(BR.listRankingIllust,illust)
    }

    private fun showItemIllust(bind : ViewDataBinding,position: Int){
        val mBind : ItemIllustBinding = bind as ItemIllustBinding
        val illust = data.content[getRealPosition(position)]
        mBind.rootView.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                itemClick?.itemClick(illust)
            }
        })
        mBind.rootView.setOnLongClickListener {
            itemLongClick?.longClick(illust)
            true
        }
        mBind.cover.setImageURI(illust.imageUrls.medium)
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    300f , context.resources.displayMetrics).toInt()
        }
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike?.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike?.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })
        //bind
        bind.setVariable(BR.illustPageCount,illust.metaPages.size)
    }

    private fun showItemManga(bind : ViewDataBinding,position: Int){
        val mBind : ItemMangaBinding = bind as ItemMangaBinding
        val illust = data.content[getRealPosition(position)]
        mBind.rootView.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                itemClick?.itemClick(illust)
            }
        })
        mBind.cover.setImageURI(illust.imageUrls.medium)
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    240f, context.resources.displayMetrics).toInt()
        }
        mBind.collectButton.isLiked = illust.isBookmarked
        mBind.collectButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike?.like(illust.pixivId,getRealPosition(position),false,likeButton)
            }
            override fun unLiked(likeButton: LikeButton) {
                itemLike?.unlike(illust.pixivId,getRealPosition(position),false,likeButton)
            }
        })
        bind.setVariable(BR.mangaIllust,illust)
    }

    enum class Type(val value : Int){
        ILLUST(0),
        MANGA(1),
        RANK(2)
    }
}