package info.hzvtc.hipixiv.adapter

import com.like.LikeButton

interface ItemLike {
    fun like(pixivId : Int,likeButton: LikeButton)
    fun unlike(pixivId : Int,likeButton: LikeButton)
}