package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.pojo.illust.IllustResponse

class IllustAdapter(private var context: Context) : BaseRecyclerViewAdapter(context = context) {

    var nextUrl = ""

    private lateinit var data : IllustResponse
    private lateinit var itemClick : IllustItemClick
    private lateinit var itemLike : ItemLike

    private var relPosition = 0
    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(data: IllustResponse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
            relPosition = 0
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

    fun addMoreData(moreData: IllustResponse){
        positionStart = typeList.size + 1
        moreDataSize = moreData.content.size

        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        for(index in 0..moreData.content.size-1){
            typeList.add(ITEM_ILLUST)
        }
        data.content.addAll(moreData.content)
    }

    fun updateUI(isNew : Boolean){
        if(isNew) notifyItemRangeRemoved(0, tempTypeListSize)
        notifyItemRangeInserted(positionStart, moreDataSize)
    }

    fun getFull(position: Int) = typeList[position] == ITEM_ILLUST

    fun setItemClick(itemClick: IllustItemClick){
        this.itemClick = itemClick
    }

    fun setItemLike(itemLike: ItemLike){
        this.itemLike = itemLike
    }

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
        adapter.setItemClick(itemClick)
        adapter.setItemLike(itemLike)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun showItemIllust(bind : ViewDataBinding,position: Int){
        val root = bind.root
        val cover: SimpleDraweeView = root.findViewById(R.id.cover) as SimpleDraweeView
        val like : LikeButton = root.findViewById(R.id.collect_button) as LikeButton
        val illust = data.content[getRelPosition(position)]
        //View
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            cover.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    300f, context.resources.displayMetrics).toInt()
            //todo 横屏使用large图片
        }
        cover.setImageURI(illust.imageUrls.medium)

        like.isLiked = illust.isBookmarked
        like.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                itemLike.like(illust.pixivId,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                itemLike.unlike(illust.pixivId,likeButton)
            }
        })
        //Bind
        bind.setVariable(BR.illust,illust)
        bind.setVariable(BR.pageCountValue,context.getString(R.string.icon_page) + illust.pageCount)
        bind.setVariable(BR.illustItemClick,itemClick)

    }
}
