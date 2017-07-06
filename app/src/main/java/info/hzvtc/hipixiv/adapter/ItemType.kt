package info.hzvtc.hipixiv.adapter

enum class ItemType(val value : Int) {
    ITEM_RANKING_TOP(0),
    ITEM_RANKING(1),
    ITEM_RANKING_ILLUST(2),
    ITEM_ILLUST_TOP(3),
    ITEM_ILLUST(4),
    ITEM_MANGA(5),
    ITEM_PROGRESS(6),
    ITEM_ILLUST_MUTED(7),
    ITEM_MANGA_MUTED(8),
    ITEM_RANKING_MUTED(9),
    ITEM_USER_MUTED(10),
    ITEM_USER(11),
    ITEM_BOOKMARK_TAG(12),
    ITEM_BOOKMARK_TAG_SELECTED(13),
    ITEM_LIST_RANKING_ILLUST(14),
    ITEM_PIXIVISION(15),
    ITEM_TREND_TAG(16),
    ITEM_TREND_TAG_TOP(17),
    ITEM_NO_DATA(18),
    ITEM_LOAD_ERROR(19),
    ITEM_META_SINGLE(20),
    ITEM_META_MULTIPLE(21),
    ITEM_CONTENT_ILLUST_USER(22)
}