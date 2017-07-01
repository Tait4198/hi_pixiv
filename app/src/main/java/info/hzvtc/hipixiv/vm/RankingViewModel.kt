package info.hzvtc.hipixiv.vm

import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.*
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.ActivityRankingBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.RankingActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import javax.inject.Inject

class RankingViewModel @Inject constructor() : BaseViewModel<RankingActivity, ActivityRankingBinding>(){

    var obsIllustNewData : Observable<IllustResponse>? = null
    var isIllust = true
    lateinit var account: Account
    lateinit var apiService : ApiService

    private var allowLoadMore = true
    private var errorIndex = 0
    private lateinit var adapter : IllustAdapter

    override fun initViewModel() {
        if(isIllust) initIllustView()
    }

    private fun initIllustView(){
        val layoutManger = GridLayoutManager(mView,2)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(pos: Int): Int = if(adapter.getFull(pos)) 1 else layoutManger.spanCount
        }
        mBind.recyclerView.layoutManager = layoutManger
        adapter = IllustAdapter(mView,false,true)
        adapter.setItemClick(
                itemClick = object : ItemClick {
                    override fun itemClick(illust: Illust) {
                        AppMessage.toastMessageShort(illust.title,mView)
                    }
                }
        )
        adapter.setItemLike(itemLike = object : ItemLike {
            override fun like(id: Int, itemIndex: Int,isRank: Boolean, likeButton: LikeButton) {
                postLikeOrUnlike(id, itemIndex,true,isRank,likeButton)
            }

            override fun unlike(id: Int,itemIndex : Int,isRank: Boolean,likeButton: LikeButton) {
                postLikeOrUnlike(id,itemIndex,false,isRank,likeButton)
            }
        })

        mBind.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    if(isIllust) getMoreIllustData()
                }
            }
            override fun scrollUp(dy: Int) {
                mView.showFab(false)
            }
            override fun scrollDown(dy: Int) {
                mView.showFab(true)
            }
        })
        mBind.recyclerView.adapter = adapter
        getIllustData(obsIllustNewData)
    }

     fun getIllustData(obs : Observable<IllustResponse>?){
        if(obs != obsIllustNewData) obsIllustNewData = obs
        Observable.just(obs)
                .doOnNext({ errorIndex = 0 })
                .filter({obs -> obs != null})
                .doOnNext({ mBind.recyclerView.visibility = View.GONE})
                .doOnNext({ mBind.progressBar.visibility = View.VISIBLE })
                .observeOn(Schedulers.io())
                .flatMap({ obs -> obs })
                .doOnNext({ illustResponse -> adapter.setNewData(illustResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _ -> adapter.updateUI(true)
                },{
                    error ->
                    mBind.recyclerView.visibility = View.VISIBLE
                    mBind.progressBar.visibility = View.GONE
                    processError(error)
                },{
                    mBind.recyclerView.visibility = View.VISIBLE
                    mBind.progressBar.visibility = View.GONE
                })
    }

    fun getMoreIllustData(){
        Observable.just(adapter.nextUrl?:"")
                .doOnNext({ errorIndex = 1 })
                .doOnNext({ allowLoadMore = false })
                .filter({ url -> !url.isNullOrEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ adapter.setProgress(true) })
                .observeOn(Schedulers.io())
                .flatMap({ account.obsToken(mView) })
                .flatMap({ token -> apiService.getIllustNext(token,adapter.nextUrl?:"")})
                .doOnNext({ illustResponse -> adapter.addMoreData(illustResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ (content) ->
                    if (content.size == 0) {
                        AppMessage.toastMessageLong(mView.getString(R.string.no_more_data), mView)
                    }
                })
                .doOnNext({ illustResponse ->
                    if(illustResponse.nextUrl.isNullOrEmpty()){
                        AppMessage.toastMessageLong(mView.getString(R.string.is_last_data), mView)
                    }
                })
                .subscribe({
                    _ ->
                    adapter.setProgress(false)
                    adapter.updateUI(false)
                },{
                    error->
                    adapter.setProgress(false)
                    allowLoadMore = true
                    processError(error)
                },{
                    allowLoadMore = true
                })
    }


    private fun postLikeOrUnlike(illustId : Int,position : Int,isLike : Boolean,isRank : Boolean,likeButton: LikeButton){
        account.obsToken(mView)
                .filter({ AppUtil.isNetworkConnected(mView) })
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
                    error -> processError(error)
                    adapter.updateBookmarked(position,false,isRank)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(mView)){
                        adapter.updateBookmarked(position,false,isRank)
                        likeButton.isLiked = false
                    }
                })
    }

    private fun processError(error : Throwable){
        Log.e("Error",error.printStackTrace().toString())
        if(AppUtil.isNetworkConnected(mView)){
            val msg = if(error is SocketTimeoutException)
                mView.getString(R.string.load_data_timeout)
            else
                mView.getString(R.string.load_data_failed)
            Snackbar.make(mBind.rootView, msg, Snackbar.LENGTH_LONG)
                    .setAction(mView.getString(R.string.app_dialog_ok),{
                        if(errorIndex == 0){
                            if(isIllust) getIllustData(obsIllustNewData)
                        }else if(errorIndex == 1){
                            if(isIllust) getMoreIllustData()
                        }
                    }).show()
        }
    }

}
