package info.hzvtc.hipixiv.vm.fragment

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.events.ItemLike
import info.hzvtc.hipixiv.adapter.events.OnScrollListener
import info.hzvtc.hipixiv.adapter.UserAdapter
import info.hzvtc.hipixiv.adapter.events.ItemClick
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.user.UserResponse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import javax.inject.Inject

class UserViewModel @Inject constructor(val apiService : ApiService,val gson: Gson) :
        BaseFragmentViewModel<BaseFragment<FragmentListBinding>, FragmentListBinding>(),ViewModelData<UserResponse> {

    var obsNewData : Observable<UserResponse>? = null
    lateinit var account: Account

    private var allowLoadMore = true
    private var errorIndex = 0
    private lateinit var adapter : UserAdapter

    override fun runView() {
        getData(obsNewData)
    }

    override fun initViewModel() {
        adapter = UserAdapter(getContext())
        adapter.setPreviewClick(object : ItemClick{
            override fun itemClick(illust: Illust) {
                val intent = Intent(getString(R.string.activity_content))
                intent.putExtra(getString(R.string.extra_json),gson.toJson(illust))
                intent.putExtra(getString(R.string.extra_type),getString(R.string.extra_type_illust))
                ActivityCompat.startActivity(getContext(), intent, null)
            }
        })

        adapter.setUserFollow(userFollow = object : ItemLike {
            override fun like(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,itemIndex,true,likeButton)
            }

            override fun unlike(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,itemIndex,false,likeButton)
            }

        })
        mBind.recyclerView.layoutManager = GridLayoutManager(getContext(),1)
        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.primary))
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

    override fun getData(obs: Observable<UserResponse>?) {
        if(obs != null){
            Observable.just(obs)
                    .doOnNext({ errorIndex = 0 })
                    .doOnNext({ if(obs != obsNewData) obsNewData = obs })
                    .doOnNext({ mBind.srLayout.isRefreshing = true })
                    .observeOn(Schedulers.io())
                    .flatMap({ observable -> observable })
                    .doOnNext({ userResponse -> adapter.setNewData(userResponse) })
                    .observeOn(AndroidSchedulers.mainThread())
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

    override fun getMoreData() {
        Observable.just(adapter.nextUrl?:"")
                .doOnNext({ errorIndex = 1 })
                .doOnNext({ allowLoadMore = false })
                .filter({ url -> !url.isNullOrEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ adapter.setProgress(true) })
                .observeOn(Schedulers.io())
                .flatMap({ account.obsToken(getContext()) })
                .flatMap({ token -> apiService.getUserNext(token,adapter.nextUrl?:"")})
                .doOnNext({ userResponse -> adapter.addMoreData(userResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ (userPreviews) ->
                    if (userPreviews.size == 0){
                        AppMessage.toastMessageLong(getString(R.string.no_more_data), getContext())
                    }
                })
                .doOnNext({ userResponse ->
                    if(userResponse.nextUrl.isNullOrEmpty()){
                        AppMessage.toastMessageLong(getString(R.string.is_last_data), getContext())
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

    private fun postFollowOrUnfollow(userId : Int,position : Int,isLike : Boolean,likeButton: LikeButton){
        account.obsToken(getContext())
                .filter({ AppUtil.isNetworkConnected(getContext()) })
                .flatMap({
                    token ->
                    if(isLike){
                        return@flatMap apiService.postFollowUser(token,userId,"public")
                    }else{
                        return@flatMap apiService.postUnfollowUser(token,userId)
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.updateFollowed(position,isLike)
                },{
                    error -> processError(error)
                    adapter.updateFollowed(position,false)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(getContext())){
                        adapter.updateFollowed(position,false)
                        likeButton.isLiked = false
                    }
                })
    }

    private fun processError(error : Throwable){
        Log.e("Error",error.printStackTrace().toString())
        if(AppUtil.isNetworkConnected(getContext())){
            val msg = if(error is SocketTimeoutException)
                getString(R.string.load_data_timeout)
            else
                getString(R.string.load_data_failed)
            Snackbar.make(getParent()?.getRootView()?:mBind.root.rootView, msg, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.app_dialog_ok),{
                        if(errorIndex == 0){
                            getData(obsNewData)
                        }else if(errorIndex == 1){
                            getMoreData()
                        }
                    }).show()
        }
    }
}