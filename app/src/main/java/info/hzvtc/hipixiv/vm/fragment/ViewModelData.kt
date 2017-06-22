package info.hzvtc.hipixiv.vm.fragment

import io.reactivex.Observable

interface ViewModelData<T> {
    fun getData(obs : Observable<T>?)

    fun getMoreData()
}