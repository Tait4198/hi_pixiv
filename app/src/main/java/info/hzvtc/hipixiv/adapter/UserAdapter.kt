package info.hzvtc.hipixiv.adapter

import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.generic.RoundingParams
import com.like.LikeButton
import com.like.OnLikeListener
import info.hzvtc.hipixiv.BR
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ItemProgressBinding
import info.hzvtc.hipixiv.databinding.ItemUserBinding
import info.hzvtc.hipixiv.databinding.ItemUserMutedBinding
import info.hzvtc.hipixiv.pojo.user.UserResponse

class UserAdapter(val context: Context) : BaseRecyclerViewAdapter(context = context) {

    var nextUrl : String? = ""

    private lateinit var data : UserResponse

    private var positionStart = 0
    private var moreDataSize = 0
    private var tempTypeListSize = 0
    private var tempProgressIndex = 0
    private var userFollow : ItemLike? = null

    init {
        setHasStableIds(true)
    }

    fun setNewData(newData: UserResponse) {
        //Clear
        if(typeList.size > 0) {
            tempTypeListSize = typeList.size
            typeList.clear()
        }
        //Set update index
        positionStart = typeList.size
        moreDataSize = 0
        //NextUrl
        if(!newData.nextUrl.isNullOrEmpty()) nextUrl = newData.nextUrl else nextUrl = ""
        //Init typeList
        val max = newData.userPreviews.size-1
        var jump = 0
        for(index in 0..max){
            if(!newData.userPreviews[index-jump].isMuted){
                moreDataSize++
                typeList.add(ItemType.ITEM_USER)
            }else{
                newData.userPreviews.removeAt(index-jump)
                jump++
            }
        }
        this.data = newData
    }

    fun addMoreData(moreData: UserResponse){
        positionStart = typeList.size + 1
        moreDataSize = 0
        nextUrl = if(!data.nextUrl.isNullOrEmpty()) moreData.nextUrl else ""
        val max = moreData.userPreviews.size-1
        var jump = 0
        for(index in 0..max){
            if(!moreData.userPreviews[index-jump].isMuted){
                moreDataSize++
                typeList.add(ItemType.ITEM_USER)
            }else{
                moreData.userPreviews.removeAt(index-jump)
                jump++
            }
        }
        data.userPreviews.addAll(moreData.userPreviews)
    }

    fun updateUI(isNew : Boolean){
        if(isNew) notifyItemRangeRemoved(0, tempTypeListSize)
        notifyItemRangeInserted(positionStart, moreDataSize)
    }

    fun updateFollowed(position: Int,isFollowed : Boolean){
        if(position <= data.userPreviews.size){
            data.userPreviews[position].user.isFollowed = isFollowed
        }
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

    fun setUserFollow(userFollow: ItemLike){
        this.userFollow = userFollow
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var holder : BindingHolder<ViewDataBinding>? = null
        when(viewType) {
            ItemType.ITEM_USER.value -> {
                holder = BindingHolder<ItemUserBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_user, parent, false), ItemType.ITEM_USER)
            }
            ItemType.ITEM_PROGRESS.value -> {
                holder = BindingHolder<ItemProgressBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_progress,parent,false),ItemType.ITEM_PROGRESS)
            }
            ItemType.ITEM_USER_MUTED.value -> {
                holder = BindingHolder<ItemUserMutedBinding>(DataBindingUtil.inflate(mLayoutInflater,
                        R.layout.item_user_muted, parent, false), ItemType.ITEM_USER_MUTED)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = (holder as BindingHolder<ViewDataBinding>).type
        when(type){
            ItemType.ITEM_USER -> showItemUser(holder.bind,position)
            else -> {
                //
            }
        }
    }

    private fun showItemUser(bind : ViewDataBinding,position: Int) {
        val mBind : ItemUserBinding = bind as ItemUserBinding
        val preview = data.userPreviews[position]
        if(context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mBind.rootView.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    230f , context.resources.displayMetrics).toInt()
            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f , context.resources.displayMetrics).toInt()
            mBind.preview1.layoutParams.height = height
            mBind.preview2.layoutParams.height = height
            mBind.preview3.layoutParams.height = height
        }
        val roundingParams = RoundingParams()
        roundingParams.setBorder(ContextCompat.getColor(context,R.color.colorTextSecond), 2f)
        roundingParams.roundAsCircle = true
        mBind.userProfile.hierarchy.roundingParams = roundingParams
        mBind.userProfile.setImageURI(preview.user.profile.medium)
        val images = arrayOf(mBind.preview1,mBind.preview2,mBind.preview3)
        val max = if(preview.illustList.size <= 3) preview.illustList.size-1 else 2
        for(index in 0..max){
            images[index].setImageURI(preview.illustList[index].imageUrls.square)
            images[index].visibility = View.VISIBLE
        }
        mBind.likeButton.isLiked = preview.user.isFollowed
        mBind.likeButton.setOnLikeListener(object : OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                userFollow?.like(preview.user.userId,position,false,likeButton)
            }
            override fun unLiked(likeButton: LikeButton) {
                userFollow?.unlike(preview.user.userId,position,false,likeButton)
            }
        })
        bind.setVariable(BR.username,preview.user.name)
    }
}