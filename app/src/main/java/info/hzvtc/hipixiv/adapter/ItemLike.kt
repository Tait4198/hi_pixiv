package info.hzvtc.hipixiv.adapter

import com.like.LikeButton

interface ItemLike {
    fun like(pixivId : Int,itemIndex : Int,isRank : Boolean,likeButton: LikeButton)
    fun unlike(pixivId : Int,itemIndex : Int,isRank : Boolean,likeButton: LikeButton)
}