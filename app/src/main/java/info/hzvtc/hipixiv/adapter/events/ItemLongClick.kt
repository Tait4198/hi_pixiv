package info.hzvtc.hipixiv.adapter.events

import info.hzvtc.hipixiv.pojo.illust.Illust

interface ItemLongClick {
    fun longClick(illust : Illust,position : Int)
}