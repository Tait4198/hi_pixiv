package info.hzvtc.hipixiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.like.LikeButton
import com.like.OnLikeListener
import com.zzhoujay.richtext.RichText
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.*
import info.hzvtc.hipixiv.databinding.*
import info.hzvtc.hipixiv.pojo.comment.Comment
import info.hzvtc.hipixiv.pojo.comment.CommentResponse
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.util.LoadingDrawable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ContentIllustAdapter(val context : Context,val type : Type) : BaseRecyclerViewAdapter(context) {

    var nextUrl : String? = null

    private lateinit var illust : Illust
    private lateinit var comments : MutableList<Comment>
    private lateinit var relatedList : MutableList<Illust>
    private var positionStart = 0

    private val originalUrls : ArrayList<String> by lazy { ArrayList<String>() }

    private var imageClick : ImageClick? = null
    private var tagClick : TagItemClick? = null
    private var relatedItemClick : ItemClick? = null
    private var userFollow : ItemLike? = null
    private var relatedItemLike : ItemLike? = null
    private var relatedTopClick : RankingTopClick? = null

    private var frontPosition = 0
    private var relatedPosition = 0
    private var commentPosition = 0
    private var moreDataSize = 0
    private var tempProgressIndex = 0

    init {
        setHasStableIds(true)
    }

    fun setNewData(illust : Illust){
        this.illust = illust
        if(type == Type.INFO){
            typeList.add(ItemType.ITEM_CONTENT_ILLUST_USER)
            frontPosition++
            if(illust.caption != ""){
                typeList.add(ItemType.ITEM_CONTENT_ILLUST_CAPTION)
                frontPosition++
            }
            typeList.add(ItemType.ITEM_CONTENT_ILLUST_INFO)
            frontPosition++
            if(illust.tags.isNotEmpty()){
                typeList.add(ItemType.ITEM_CONTENT_ILLUST_TAG)
                frontPosition++
            }
            typeList.add(ItemType.ITEM_CONTENT_RELATED_TOP)
            frontPosition++
            typeList.add(ItemType.ITEM_CONTENT_PROGRESS)
            relatedPosition = typeList.size
            frontPosition++
            typeList.add(ItemType.ITEM_CONTENT_COMMENTS_TOP)
            frontPosition++
            typeList.add(ItemType.ITEM_CONTENT_PROGRESS)
            commentPosition = typeList.size
        }else if(type == Type.META){
            if(illust.pageCount == 1){
                typeList.add(ItemType.ITEM_META_SINGLE)
                originalUrls.add(illust.metaSinglePage.original)
            }else{
                for(index in illust.metaPages.indices){
                    typeList.add(ItemType.ITEM_META_MULTIPLE)
                    originalUrls.add(illust.metaPages[index].imageUrls.original)
                }
            }
        }
        notifyItemRangeInserted(positionStart, typeList.size)
    }

    fun setNewComment(newData: CommentResponse){
        this.comments = newData.comments
        typeList.removeAt(commentPosition-1)
        if(!newData.nextUrl.isNullOrEmpty()) nextUrl = newData.nextUrl else nextUrl = ""
        commentPosition = typeList.size + 1
        moreDataSize = 0
        if(newData.comments.isNotEmpty()){
            newData.comments.forEach {
                moreDataSize++
                typeList.add(ItemType.ITEM_CONTENT_ILLUST_COMMENT)
            }
        }else{
            moreDataSize++
            typeList.add(ItemType.ITEM_CONTENT_NO_COMMENTS)
        }
    }

    fun setNoComments(){
        moreDataSize = 1
        typeList[commentPosition-1] = ItemType.ITEM_CONTENT_NO_COMMENTS
        updateComments()
    }

    fun addMoreComment(moreData : CommentResponse){
        commentPosition = typeList.size + 1
        moreDataSize = 0
        nextUrl = if(!moreData.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        moreData.comments.forEach {
            moreDataSize++
            typeList.add(ItemType.ITEM_CONTENT_ILLUST_COMMENT)
        }
        comments.addAll(moreData.comments)
    }

    fun setNewRelated(newList : MutableList<Illust>){
        this.relatedList = newList.subList(0,if(newList.size > 8) 8 else newList.size-1)
        typeList[relatedPosition-1] = ItemType.ITEM_CONTENT_RELATED
        notifyItemChanged(relatedPosition)
    }

    fun updateComments(){
        notifyItemRangeInserted(commentPosition, moreDataSize)
    }

    fun setImageClick(imageClick: ImageClick){
        this.imageClick = imageClick
    }

    fun setTagClick(tagItemClick: TagItemClick){
        this.tagClick = tagItemClick
    }

    fun setUserFollow(userFollow : ItemLike){
        this.userFollow = userFollow
    }

    fun setRelatedItemClick(relatedItemClick: ItemClick){
        this.relatedItemClick = relatedItemClick
    }

    fun setRelatedItemLike(relatedItemLike : ItemLike){
        this.relatedItemLike = relatedItemLike
    }

    fun setRelatedTopClick(relatedTopClick: RankingTopClick){
        this.relatedTopClick = relatedTopClick
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

    private fun getRealPosition(position : Int) = position - frontPosition

    fun updateRelatedLike(position: Int,isFollowed : Boolean){
        if(position <= relatedList.size && position >= 0){
            relatedList[position].isBookmarked = isFollowed
        }
    }

    fun updateFollowed(isFollowed: Boolean){
        illust.user.isFollowed = isFollowed
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        when(type) {
            ItemType.ITEM_CONTENT_ILLUST_USER -> showItemUser(holder.bind)
            ItemType.ITEM_META_SINGLE -> showItemMetaSingle(holder.bind)
            ItemType.ITEM_META_MULTIPLE -> showItemMetaMultiple(holder.bind,position)
            ItemType.ITEM_CONTENT_ILLUST_CAPTION -> showItemContentCaption(holder.bind)
            ItemType.ITEM_CONTENT_ILLUST_INFO -> showItemContentInfo(holder.bind)
            ItemType.ITEM_CONTENT_ILLUST_TAG -> showItemContentTag(holder.bind)
            ItemType.ITEM_CONTENT_ILLUST_COMMENT -> showItemContentComment(holder.bind,position)
            ItemType.ITEM_CONTENT_RELATED -> showItemContentRelated(holder.bind)
            ItemType.ITEM_CONTENT_RELATED_TOP -> showItemRelatedTop(holder.bind)
            else -> {
                //
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType) {
            ItemType.ITEM_CONTENT_ILLUST_USER.value -> {
                holder = BindingHolder<ItemContentIllustUserBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_illust_user, parent, false), ItemType.ITEM_CONTENT_ILLUST_USER)
            }
            ItemType.ITEM_META_SINGLE.value -> {
                holder = BindingHolder<ItemMetaSingleBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_meta_single, parent, false), ItemType.ITEM_META_SINGLE)
            }
            ItemType.ITEM_META_MULTIPLE.value -> {
                holder = BindingHolder<ItemMetaMultipleBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_meta_multiple, parent, false), ItemType.ITEM_META_MULTIPLE)
            }
            ItemType.ITEM_CONTENT_ILLUST_CAPTION.value->{
                holder = BindingHolder<ItemContentIllustCaptionBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_illust_caption, parent, false), ItemType.ITEM_CONTENT_ILLUST_CAPTION)
            }
            ItemType.ITEM_CONTENT_ILLUST_INFO.value->{
                holder = BindingHolder<ItemContentIllustInfoBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_illust_info, parent, false), ItemType.ITEM_CONTENT_ILLUST_INFO)
            }
            ItemType.ITEM_CONTENT_ILLUST_TAG.value->{
                holder = BindingHolder<ItemContentIllustTagBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_illust_tag, parent, false), ItemType.ITEM_CONTENT_ILLUST_TAG)
            }
            ItemType.ITEM_CONTENT_ILLUST_COMMENT.value->{
                holder = BindingHolder<ItemContentCommentBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_comment, parent, false), ItemType.ITEM_CONTENT_ILLUST_COMMENT)
            }
            ItemType.ITEM_CONTENT_PROGRESS.value->{
                holder = BindingHolder<ItemContentProgressBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_progress, parent, false), ItemType.ITEM_CONTENT_PROGRESS)
            }
            ItemType.ITEM_PROGRESS.value -> {
                holder = BindingHolder<ItemProgressBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_progress,parent,false),ItemType.ITEM_PROGRESS)
            }
            ItemType.ITEM_CONTENT_RELATED_TOP.value -> {
                holder = BindingHolder<ItemRelatedTopBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_related_top,parent,false),ItemType.ITEM_CONTENT_RELATED_TOP)
            }
            ItemType.ITEM_CONTENT_RELATED.value -> {
                holder = BindingHolder<ItemRelatedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_related,parent,false),ItemType.ITEM_CONTENT_RELATED)
            }
            ItemType.ITEM_CONTENT_NO_COMMENTS.value -> {
                holder = BindingHolder<ItemContentNoCommentsBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_no_comments,parent,false),ItemType.ITEM_CONTENT_NO_COMMENTS)
            }
            ItemType.ITEM_CONTENT_COMMENTS_TOP.value -> {
                holder = BindingHolder<ItemContentCommentsTopBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_content_comments_top,parent,false),ItemType.ITEM_CONTENT_COMMENTS_TOP)
            }
        }
        return holder
    }

    private fun showItemMetaSingle(bind: ViewDataBinding) {
        val mBind : ItemMetaSingleBinding = bind as ItemMetaSingleBinding
        val hierarchy = GenericDraweeHierarchyBuilder(context.resources).setProgressBarImage(LoadingDrawable()).build()
        mBind.image.hierarchy = hierarchy
        mBind.image.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        mBind.image.setImageURI(illust.imageUrls.large)
        mBind.image.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                imageClick?.imageClick(0,originalUrls)
            }
        })
    }

    private fun showItemMetaMultiple(bind : ViewDataBinding,position: Int){
        val mBind : ItemMetaMultipleBinding = bind as ItemMetaMultipleBinding
        val hierarchy = GenericDraweeHierarchyBuilder(context.resources).setProgressBarImage(LoadingDrawable()).build()
        mBind.image.hierarchy = hierarchy
        mBind.image.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        mBind.image.setImageURI(illust.metaPages[position].imageUrls.large)
        mBind.pageCount.text = (position + 1).toString()
        mBind.image.setOnClickListener(object : CheckDoubleClickListener() {
            override fun click(v: View?) {
                imageClick?.imageClick(position,originalUrls)
            }
        })
    }

    private fun showItemUser(bind : ViewDataBinding){
        val mBind : ItemContentIllustUserBinding = bind as ItemContentIllustUserBinding
        mBind.name.text = illust.user.name
        mBind.title.text = illust.title
        val roundingParams = RoundingParams()
        roundingParams.setBorder(ContextCompat.getColor(context,R.color.colorTextSecond), 2f)
        roundingParams.roundAsCircle = true
        mBind.userProfile.hierarchy.roundingParams = roundingParams
        mBind.userProfile.setImageURI(illust.user.profile.medium)
        mBind.likeButton.isLiked = illust.user.isFollowed
        mBind.likeButton.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                userFollow?.like(illust.user.userId,-1,false,likeButton)
            }

            override fun unLiked(likeButton: LikeButton) {
                userFollow?.unlike(illust.user.userId,-1,false,likeButton)
            }
        })
    }

    private fun showItemContentCaption(bind: ViewDataBinding){
        val mBind : ItemContentIllustCaptionBinding = bind as ItemContentIllustCaptionBinding
        RichText.from(illust.caption)
                .urlClick { true }
                .into(mBind.caption)
    }

    private fun showItemContentTag(bind: ViewDataBinding) {
        val mBind : ItemContentIllustTagBinding = bind as ItemContentIllustTagBinding
        val tagList = ArrayList<String>()
        illust.tags.forEach({ (name) -> tagList.add(name)})
        mBind.tagGroup.setTags(tagList)
        mBind.tagGroup.setOnTagClickListener { tag -> tagClick?.itemClick(0,tag) }
    }

    private fun showItemContentInfo(bind: ViewDataBinding) {
        val mBind : ItemContentIllustInfoBinding = bind as ItemContentIllustInfoBinding
        mBind.viewCount.text = illust.view.toString()
        mBind.bookmarksCount.text = illust.bookmarks.toString()
        val toolsStr = StringBuilder("")
        if (illust.tools.isNotEmpty()) toolsStr.append(" · ")
        illust.tools.forEach{to -> toolsStr.append(to).append(" ")}
        val other = "${dateFormat(illust.createDate)} · ${illust.width} × ${illust.height} $toolsStr"
        mBind.otherInfo.text = other
    }


    private fun showItemContentRelated(bind: ViewDataBinding) {
        val mBind : ItemRelatedBinding = bind as ItemRelatedBinding
        val adapter = RankingAdapter(context,RankingAdapter.Type.RELATED)
        adapter.setNewData(relatedList)
        adapter.setItemClick(relatedItemClick)
        adapter.setItemLike(relatedItemLike)
        mBind.illustRecycler.adapter = adapter
        mBind.illustRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    private fun showItemContentComment(bind: ViewDataBinding,position: Int) {
        val mBind : ItemContentCommentBinding = bind as ItemContentCommentBinding
        val comment = comments[getRealPosition(position)]
        val roundingParams = RoundingParams()
        roundingParams.setBorder(ContextCompat.getColor(context,R.color.colorTextSecond), 2f)
        roundingParams.roundAsCircle = true
        mBind.userProfile.hierarchy.roundingParams = roundingParams
        mBind.userProfile.setImageURI(comment.user.profile.medium)
        mBind.commentUserName.text = comment.user.name
        mBind.commentContent.text = comment.comment
        mBind.commentDate.text = dateFormat(comment.date)
    }

    private fun  showItemRelatedTop(bind: ViewDataBinding) {
        val mBind : ItemRelatedTopBinding = bind as ItemRelatedTopBinding
        val relatedClick = object : CheckDoubleClickListener(){
            override fun click(v: View?) {
                relatedTopClick?.itemClick(RankingType.ILLUST)
            }
        }
        mBind.root.setOnClickListener(relatedClick)
        mBind.seeMore.setOnClickListener(relatedClick)
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateFormat(time: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val output = SimpleDateFormat(context.getString(R.string.out_format))
        val d: Date = sdf.parse(time)
        return output.format(d)
    }

    enum class Type(val value : Int){
        META(0),INFO(1)
    }
}