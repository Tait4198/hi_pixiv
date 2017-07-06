package info.hzvtc.hipixiv.adapter.events

import info.hzvtc.hipixiv.adapter.RankingType

interface RankingTopClick {
    fun itemClick(type : RankingType)
}