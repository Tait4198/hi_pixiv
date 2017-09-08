package info.hzvtc.hipixiv.vm.fragment

import android.content.Intent
import android.net.Uri
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.like.LikeButton
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.ContentIllustAdapter
import info.hzvtc.hipixiv.adapter.RankingType
import info.hzvtc.hipixiv.adapter.events.*
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.FragmentContentIllustBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ContentIllustViewModel @Inject constructor(val account: Account, val apiService: ApiService, private val userPreferences: UserPreferences, val gson: Gson)
    : BaseFragmentViewModel<BaseFragment<FragmentContentIllustBinding>, FragmentContentIllustBinding>(){

    lateinit var illust : Illust

    private lateinit var metaAdapter : ContentIllustAdapter
    private lateinit var contentAdapter : ContentIllustAdapter

    private var allowLoadMore = false
    private var isBookmarked = false

    override fun runView() {
        Observable.just(userPreferences.isPremium?:false)
                .filter{ premium -> premium}
                .flatMap { account.obsToken(getContext()) }
                .flatMap { token -> apiService.postAddIllustBrowsingHistory(token,getIdList(illust.pixivId)) }
                .subscribe({
                    //
                },{
                    error -> Log.d("Error",error.toString())
                })
        getRelated()
        getComments()
    }

    override fun initViewModel() {
        metaAdapter = ContentIllustAdapter(getContext(), ContentIllustAdapter.Type.META)
        contentAdapter = ContentIllustAdapter(getContext(), ContentIllustAdapter.Type.INFO)

        val resId = if (illust.isBookmarked) R.drawable.favorite_on else R.drawable.favorite_off
        isBookmarked = illust.isBookmarked
        mBind.fabLike.setImageResource(resId)
        mBind.fabLike.setOnClickListener {
            postFabLikeOrUnlike(illust.pixivId,!isBookmarked)
        }

        mBind.imageRecycler.layoutManager = LinearLayoutManager(getContext())
        mBind.imageRecycler.adapter = metaAdapter
        metaAdapter.setNewData(illust)
        metaAdapter.setImageClick(object : ImageClick{
            override fun imageClick(position: Int, urlsList: ArrayList<String>) {
                val intent = Intent(getString(R.string.activity_image))
                intent.putExtra(getString(R.string.extra_int),position)
                intent.putStringArrayListExtra(getString(R.string.extra_list),urlsList)
                ActivityCompat.startActivity(getContext(), intent, null)
            }
        })

        mBind.bottomRecycler.layoutManager = LinearLayoutManager(getContext())
        mBind.bottomRecycler.adapter = contentAdapter
        val behavior = BottomSheetBehavior.from(mBind.bottomRecycler)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                mBind.fabLike.translationY = slideOffset * (mBind.fabLike.height * 2.5f)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        mBind.bottomRecycler.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    getMoreComments()
                }
            }
        })
        contentAdapter.setNewData(illust)

        contentAdapter.setUrlClick(object : UrlClick{
            override fun click(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ActivityCompat.startActivity(mView.activity, intent, null)
            }
        })

        contentAdapter.setTagClick(object : TagItemClick{
            override fun itemClick(position: Int, tag: String) {
                val intent = Intent(mView.getString(R.string.activity_search))
                intent.putExtra(getString(R.string.extra_search_tag),tag)
                ActivityCompat.startActivity(mView.context, intent, null)
            }
        })
        contentAdapter.setUserFollow(object : ItemLike{
            override fun like(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,true,likeButton)
            }

            override fun unlike(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postFollowOrUnfollow(id,false,likeButton)
            }
        })
        contentAdapter.setRelatedItemLike(object : ItemLike{
            override fun like(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postLikeOrUnlike(id,itemIndex,true,likeButton)
            }

            override fun unlike(id: Int, itemIndex: Int, isRank: Boolean, likeButton: LikeButton) {
                postLikeOrUnlike(id,itemIndex,false,likeButton)
            }
        })
        contentAdapter.setRelatedItemClick(object : ItemClick{
            override fun itemClick(illust: Illust) {
                val intent = Intent(mView.getString(R.string.activity_content))
                intent.putExtra(mView.getString(R.string.extra_json),gson.toJson(illust))
                intent.putExtra(getString(R.string.extra_type),mView.getString(R.string.extra_type_illust))
                ActivityCompat.startActivity(mView.context, intent, null)
            }
        })
        contentAdapter.setRelatedTopClick(object : RankingTopClick{
            override fun itemClick(type: RankingType) {
                val intent = Intent(getString(R.string.activity_related))
                intent.putExtra(getString(R.string.extra_int),illust.pixivId)
                ActivityCompat.startActivity(mView.context, intent, null)
            }
        })

        mBind.bottomRecycler.visibility = View.VISIBLE
        mBind.imageRecycler.visibility = View.VISIBLE
        mBind.fabLike.visibility = View.VISIBLE
        mBind.progressBar.visibility = View.GONE
    }

    private fun getComments(){
        account.obsToken(mView.context)
                .flatMap { token -> apiService.getIllustComments(token,illust.pixivId) }
                .doOnNext { commentResponse -> contentAdapter.setNewComment(commentResponse)}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    contentAdapter.updateComments()
                },{
                    error -> Log.e("Error",error.message)
                    contentAdapter.setNoComments()
                },{
                    allowLoadMore = true
                })
    }

    private fun getMoreComments(){
        Observable.just(contentAdapter.nextUrl?:"")
                .doOnNext({ allowLoadMore = false })
                .filter({ url -> !url.isNullOrEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ contentAdapter.setProgress(true) })
                .observeOn(Schedulers.io())
                .flatMap { account.obsToken(mView.context) }
                .flatMap { token -> apiService.getCommentsNext(token,contentAdapter.nextUrl?:"") }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { contentAdapter.setProgress(false) }
                .doOnNext { commentResponse -> contentAdapter.addMoreComment(commentResponse) }
                .subscribe({
                    contentAdapter.updateComments()
                },{
                    error ->
                    contentAdapter.setProgress(false)
                    allowLoadMore = true
                    Log.e("Error",error.message)
                },{
                    allowLoadMore = true
                })
    }

    private fun getRelated(){
        account.obsToken(mView.context)
                .flatMap { token -> apiService.getIllustRecommended(token,illust.pixivId) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    (content) -> contentAdapter.setNewRelated(content)
                },{
                    error -> Log.e("Error",error.message)
                })
    }

    private fun postFollowOrUnfollow(userId : Int,isLike : Boolean,likeButton: LikeButton){
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
                    contentAdapter.updateFollowed(isLike)
                },{
                    error -> Log.e("Error",error.message)
                    contentAdapter.updateFollowed(false)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(getContext())){
                        contentAdapter.updateFollowed(false)
                        likeButton.isLiked = false
                    }
                })
    }

    private fun postFabLikeOrUnlike(illustId : Int,isLike : Boolean){
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
                    val resId = if (isLike) R.drawable.favorite_on else R.drawable.favorite_off
                    isBookmarked = isLike
                    mBind.fabLike.setImageResource(resId)
                },{
                    error -> Log.e("Error",error.message)
                    isBookmarked = !isLike
                },{
                    if(!AppUtil.isNetworkConnected(mView.context)){
                        isBookmarked = !isLike
                    }
                })
    }

    private fun postLikeOrUnlike(illustId : Int,position : Int,isLike : Boolean,likeButton: LikeButton){
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
                    contentAdapter.updateRelatedLike(position,isLike)
                },{
                    error -> Log.e("Error",error.message)
                    contentAdapter.updateRelatedLike(position,false)
                    likeButton.isLiked = false
                },{
                    if(!AppUtil.isNetworkConnected(mView.context)){
                        contentAdapter.updateRelatedLike(position,false)
                        likeButton.isLiked = false
                    }
                })
    }

    private fun getIdList(illustId : Int) : List<Int>{
        val list = ArrayList<Int>()
        list.add(illustId)
        return list
    }
}
