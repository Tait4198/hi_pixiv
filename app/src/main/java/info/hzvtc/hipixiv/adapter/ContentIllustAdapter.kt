package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.CheckDoubleClickListener
import info.hzvtc.hipixiv.adapter.events.ImageClick
import info.hzvtc.hipixiv.databinding.ItemContentIllustUserBinding
import info.hzvtc.hipixiv.databinding.ItemMetaMultipleBinding
import info.hzvtc.hipixiv.databinding.ItemMetaSingleBinding
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.util.LoadingDrawable

class ContentIllustAdapter(val context : Context,val type : Type) : BaseRecyclerViewAdapter(context) {

    private lateinit var illust : Illust
    private var positionStart = 0

    private val originalUrls : ArrayList<String> by lazy { ArrayList<String>() }

    private var imageClick : ImageClick? = null

    init {
        setHasStableIds(true)
    }

    fun setNewData(illust : Illust){
        this.illust = illust
        if(type == Type.INFO){
            typeList.add(ItemType.ITEM_CONTENT_ILLUST_USER)
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

    fun setImageClick(imageClick: ImageClick){
        this.imageClick = imageClick
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        when(type) {
            ItemType.ITEM_CONTENT_ILLUST_USER -> showItemUser(holder.bind)
            ItemType.ITEM_META_SINGLE -> showItemMetaSingle(holder.bind)
            ItemType.ITEM_META_MULTIPLE -> showItemMetaMultiple(holder.bind,position)
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
    }

    enum class Type(val value : Int){
        META(0),INFO(1)
    }
}