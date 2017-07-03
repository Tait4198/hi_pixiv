package info.hzvtc.hipixiv.vm.fragment

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.ItemLike
import info.hzvtc.hipixiv.adapter.OnScrollListener
import info.hzvtc.hipixiv.adapter.UserAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.user.UserResponse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import javax.inject.Inject

class UserViewModel @Inject constructor(val apiService : ApiService) :
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
        adapter = UserAdapter(mView.context)
        adapter.setUserFollow(userFollow = object : ItemLike{
            override fun like(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,itemIndex,true,likeButton)
            }

            override fun unlike(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,itemIndex,false,likeButton)
            }

        })
        mBind.recyclerView.layoutManager = GridLayoutManager(mView.context,1)
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

    override fun getData(obs: Observable<UserResponse>?) {
        if(obs != null){
            Observable.just(obs)
                    .doOnNext({ errorIndex = 0 })
                    .doOnNext({ if(obs != obsNewData) obsNewData = obs })
                    .doOnNext({ mBind.srLayout.isRefreshing = true })
                    .observeOn(Schedulers.io())
                    .flatMap({ obs -> obs })
                    .doOnNext({ userResponse -> adapter.setNewData(userResponse) })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _ -> adapter.updateUI(true)
                    },{
                        error ->
                        mBind.srLayout.isRefreshing = false
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
                .flatMap({ account.obsToken(mView.context) })
                .flatMap({ token -> apiService.getUserNext(token,adapter.nextUrl?:"")})
                .doOnNext({ userResponse -> adapter.addMoreData(userResponse) })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ (userPreviews) ->
                    if (userPreviews.size == 0){
                        AppMessage.toastMessageLong(mView.getString(R.string.no_more_data), mView.context)
                    }
                })
                .doOnNext({ userResponse ->
                    if(userResponse.nextUrl.isNullOrEmpty()){
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

    private fun postFollowOrUnfollow(userId : Int,position : Int,isLike : Boolean,likeButton: LikeButton){
        account.obsToken(mView.context)
                .filter({ AppUtil.isNetworkConnected(mView.context) })
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
                    adapter.updateFollowed(position,true)
                },{
                    error -> processError(error)
                    adapter.updateFollowed(position,false)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(mView.context)){
                        adapter.updateFollowed(position,false)
                        likeButton.isLiked = false
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