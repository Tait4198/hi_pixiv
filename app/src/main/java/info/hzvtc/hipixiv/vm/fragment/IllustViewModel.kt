package info.hzvtc.hipixiv.vm.fragment

import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
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
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.IllustFragment
import info.hzvtc.hipixiv.vm.BaseFragmentViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IllustViewModel @Inject constructor(val apiService: ApiService) : BaseFragmentViewModel<IllustFragment, FragmentIllustBinding>(){

    var isManga : Boolean = false
    lateinit var obsNewData : Observable<IllustResponse>
    lateinit var account: Account

    private var allowLoadMore = true
    private lateinit var adapter : IllustAdapter

    override fun initViewModel() {
        if(isManga){
            val layoutManger = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            mBind.illustRecycler.layoutManager = layoutManger
        }else{
            val layoutManger = GridLayoutManager(mView.context,2)
            layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(pos: Int): Int =
                        if(adapter.getFull(pos)) 1 else layoutManger.spanCount
            }
            mBind.illustRecycler.layoutManager = layoutManger
        }

        adapter = IllustAdapter(mView.context,isManga)
        adapter.setItemClick(
                itemClick = object : IllustItemClick {
                    override fun click(illust: Illust) {
                        AppMessage.toastMessageShort(illust.title,mView.context)
                    }
                }
        )
        adapter.setItemLike(itemLike = object : ItemLike{
            override fun like(pixivId: Int, itemIndex: Int,isRank: Boolean, likeButton: LikeButton) {
                postLikeOrUnlike(pixivId, itemIndex,true,isRank,likeButton)
            }

            override fun unlike(pixivId: Int,itemIndex : Int,isRank: Boolean,likeButton: LikeButton) {
                postLikeOrUnlike(pixivId,itemIndex,false,isRank,likeButton)
            }
        })

        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(mView.context, R.color.primary))
        mBind.srLayout.setOnRefreshListener({ getNewData(obsNewData) })
        mBind.illustRecycler.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator
        mBind.illustRecycler.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    getMoreData()
                }
            }
        })
        mBind.illustRecycler.adapter = adapter

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
                    error -> Log.e("Error",error.toString())
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

    private fun postLikeOrUnlike(illustId : Int,position : Int,isLike : Boolean,isRank : Boolean,likeButton: LikeButton){
        account.obsToken(mView.context)
                .filter({ AppUtil.isNetworkConnected(mView.context) })
                .flatMap({
                    token ->
                    if(isLike){
                        return@flatMap apiService.postLikeIllust(token,illustId,"public")
                    }else{
                        return@flatMap apiService.postUnlikeIllust(token,illustId)
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.updateBookmarked(position,true,isRank)
                },{
                    error -> Log.e("Error",error.toString())
                    adapter.updateBookmarked(position,false,isRank)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(mView.context)){
                        adapter.updateBookmarked(position,false,isRank)
                        likeButton.isLiked = false
                    }
                })
    }
}
