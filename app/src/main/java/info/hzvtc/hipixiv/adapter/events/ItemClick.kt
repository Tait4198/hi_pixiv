package info.hzvtc.hipixiv.adapter.events

import info.hzvtc.hipixiv.pojo.illust.Illust

interface ItemClick {
    fun itemClick(illust : Illust)
}