package info.hzvtc.hipixiv.adapter.events

import com.like.LikeButton

interface ItemLike {
    fun like(id : Int,itemIndex : Int,isRank : Boolean,likeButton: LikeButton)
    fun unlike(id : Int,itemIndex : Int,isRank : Boolean,likeButton: LikeButton)
}