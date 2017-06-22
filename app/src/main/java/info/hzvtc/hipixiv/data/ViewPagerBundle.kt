package info.hzvtc.hipixiv.data

import info.hzvtc.hipixiv.view.fragment.BaseFragment

abstract class ViewPagerBundle<T : BaseFragment> {
    var nowPosition = 0
    lateinit var pagers : Array<T>
    lateinit var titles : Array<String>

    open fun fabShow(position : Int){
        nowPosition = position
    }

    open fun fabClick(){
        //
    }
}
