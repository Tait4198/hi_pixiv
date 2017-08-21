package info.hzvtc.hipixiv.vm.fragment

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.*
import info.hzvtc.hipixiv.adapter.events.*
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import javax.inject.Inject

class IllustViewModel @Inject constructor(val apiService: ApiService,val gson: Gson) :
        BaseFragmentViewModel<BaseFragment<FragmentListBinding>, FragmentListBinding>(),ViewModelData<IllustResponse>{

    var contentType : IllustAdapter.Type = IllustAdapter.Type.ILLUST
    var obsNewData : Observable<IllustResponse>? = null
    lateinit var account: Account

    private var allowLoadMore = true
    private var errorIndex = 0
    private lateinit var adapter : IllustAdapter

    override fun initViewModel() {
        if(contentType == IllustAdapter.Type.MANGA){
            val layoutManger = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            mBind.recyclerView.layoutManager = layoutManger
        }else{
            val layoutManger = GridLayoutManager(mView.context,2)
            layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(pos: Int): Int =
                        if(adapter.getFull(pos)) 1 else layoutManger.spanCount
            }
            mBind.recyclerView.layoutManager = layoutManger
        }

        adapter = IllustAdapter(mView.context,contentType)
        adapter.setItemClick(
                itemClick = object : ItemClick {
                    override fun itemClick(illust: Illust) {
                        val intent = Intent(mView.getString(R.string.activity_content))
                        intent.putExtra(mView.getString(R.string.extra_json),gson.toJson(illust))
                        intent.putExtra(getString(R.string.extra_type),mView.getString(R.string.extra_type_illust))
                        ActivityCompat.startActivity(mView.context, intent, null)
                    }
                }
        )
        adapter.setItemLongClick(
                itemLongClick = object : ItemLongClick{
                    override fun longClick(illust: Illust) {
                        AppMessage.toastMessageLong("屏蔽设定待实现...",mView.context)
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

        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(mView.context, R.color.primary))
        mBind.srLayout.setOnRefreshListener({ getData(obsNewData) })
        mBind.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    getMoreData()
                }
            }
            override fun scrollUp(dy: Int) {
                getParent()?.showFab(false)
            }
            override fun scrollDown(dy: Int) {
                getParent()?.showFab(true)
            }
        })
        mBind.recyclerView.adapter = adapter
    }

    override fun runView() {
        getData(obsNewData)
    }

    override fun getData(obs : Observable<IllustResponse>?){
        if(obs != null){
            Observable.just(obs)
                    .doOnNext({ errorIndex = 0 })
                    .doOnNext({ if(obs != obsNewData) obsNewData = obs })
                    .doOnNext({ mBind.srLayout.isRefreshing = true })
                    .observeOn(Schedulers.io())
                    .flatMap({ observable -> observable })
                    .doOnNext({ illustResponse -> adapter.setNewData(illustResponse) })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({
                        illustResponse ->
                        if(illustResponse.ranking.isNotEmpty()) rankingTopClick()
                    })
                    .subscribe({
                        _ -> adapter.updateUI(true)
                    },{
                        error ->
                        mBind.srLayout.isRefreshing = false
                        adapter.loadError()
                        processError(error)
                    },{
                        mBind.srLayout.isRefreshing = false
                    })
        }
    }

    override fun getMoreData(){
        Observable.just(adapter.nextUrl?:"")
                .doOnNext({ errorIndex = 1 })
                .doOnNext({ allowLoadMore = false })
                .filter({ url -> !url.isNullOrEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ adapter.setProgress(true) })
                .observeOn(Schedulers.io())
                .flatMap({ account.obsToken(mView.context) })
                .flatMap({ token -> apiService.getIllustNext(token,adapter.nextUrl?:"")})
                .doOnNext({ illustResponse -> adapter.addMoreData(illustResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ (content) ->
                    if (content.size == 0) {
                        AppMessage.toastMessageLong(mView.getString(R.string.no_more_data), mView.context)
                    }
                })
                .doOnNext({ illustResponse ->
                    if(illustResponse.nextUrl.isNullOrEmpty()){
                        AppMessage.toastMessageLong(mView.getString(R.string.is_last_data), mView.context)
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
                    adapter.updateBookmarked(position,isLike,isRank)
                },{
                    error -> processError(error)
                    adapter.updateBookmarked(position,false,isRank)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(mView.context)){
                        adapter.updateBookmarked(position,false,isRank)
                        likeButton.isLiked = false
                    }
                })
    }

    private fun rankingTopClick(){
        adapter.setRankingTopClick(object : RankingTopClick {
            override fun itemClick(type: RankingType) {
                val intent = Intent(mView.getString(R.string.activity_ranking))
                intent.putExtra(mView.getString(R.string.extra_string),type.value)
                ActivityCompat.startActivity(mView.context, intent, null)
            }
        })
    }

    private fun processError(error : Throwable){
        Log.e("Error",error.printStackTrace().toString())
        if(AppUtil.isNetworkConnected(mView.context)){
            val msg = if(error is SocketTimeoutException)
                mView.getString(R.string.load_data_timeout)
            else
                mView.getString(R.string.load_data_failed)
            Snackbar.make(getParent()?.getRootView()?:mBind.root.rootView, msg, Snackbar.LENGTH_LONG)
                    .setAction(mView.getString(R.string.app_dialog_ok),{
                        if(errorIndex == 0){
                            getData(obsNewData)
                        }else if(errorIndex == 1){
                            getMoreData()
                        }
                    }).show()
        }
    }
}
