package info.hzvtc.hipixiv.vm.fragment

import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.adapter.IllustItemClick
import info.hzvtc.hipixiv.adapter.ItemLike
import info.hzvtc.hipixiv.adapter.OnScrollListener
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentIllustBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.view.fragment.IllustFragment
import info.hzvtc.hipixiv.vm.BaseFragmentViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IllustViewModel @Inject constructor(val account: Account,val apiService: ApiService) : BaseFragmentViewModel<IllustFragment, FragmentIllustBinding>(){

    var obsNewData : Observable<IllustResponse>? = null

    private var allowLoadMore = true

    private lateinit var layoutManger : GridLayoutManager
    private lateinit var adapter : IllustAdapter

    override fun initViewModel() {
        layoutManger = GridLayoutManager(mView.context,2)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(pos: Int): Int =
                    if(adapter.getFull(pos)) 1 else layoutManger.spanCount
        }

        adapter = IllustAdapter(mView.context)
        adapter.setItemClick(
                itemClick = object : IllustItemClick {
                    override fun click(illust: Illust) {
                        AppMessage.toastMessageShort(illust.title,mView.context)
                    }
                }
        )
        adapter.setItemLike(itemLike = object : ItemLike{
            override fun like(pixivId: Int, likeButton: LikeButton) {
                AppMessage.toastMessageLong(pixivId.toString(),mView.context)
            }

            override fun unlike(pixivId: Int, likeButton: LikeButton) {
                AppMessage.toastMessageLong(pixivId.toString(),mView.context)
            }
        })

        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(mView.context, R.color.primary))
        mBind.srLayout.setOnRefreshListener({ getNewData(obsNewData) })
        mBind.illustRecycler.itemAnimator = DefaultItemAnimator()
        mBind.illustRecycler.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    getMoreData()
                }
            }
        })
        mBind.illustRecycler.adapter = adapter
        mBind.illustRecycler.layoutManager = layoutManger

        getNewData(obsNewData)
    }

    private fun getNewData(obs : Observable<IllustResponse>?){
       Observable.just(obs)
               .filter({obs -> obs != null})
               .doOnNext({ mBind.srLayout.isRefreshing = true })
               .flatMap({ obs -> obs })
               .doOnNext({ illustResponse -> adapter.setNewData(illustResponse) })
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe({
                   _ -> adapter.updateUI(true)
                },{
                    error -> Log.d("Error",error.toString())
                },{
                    mBind.srLayout.isRefreshing = false
                })
    }

    private fun getMoreData(){
        Observable.just(adapter.nextUrl)
                .doOnNext({ allowLoadMore = false })
                .filter({ url -> !url.isNullOrEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ adapter.setProgress(true) })
                .observeOn(Schedulers.io())
                .flatMap({ account.obsToken(mView.context) })
                .flatMap({ token -> apiService.getIllustsNext(token,adapter.nextUrl)})
                .doOnNext({ illustResponse -> adapter.addMoreData(illustResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _ ->
                    adapter.setProgress(false)
                    adapter.updateUI(false)
                },{
                    adapter.setProgress(false)
                    allowLoadMore = true
                },{
                    allowLoadMore = true
                })
    }
}
