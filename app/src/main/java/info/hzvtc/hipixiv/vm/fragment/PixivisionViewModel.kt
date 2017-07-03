package info.hzvtc.hipixiv.vm.fragment

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.OnScrollListener
import info.hzvtc.hipixiv.adapter.PixivisionAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.pixivision.PixivisionResopnse
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import javax.inject.Inject

class PixivisionViewModel @Inject constructor(val apiService: ApiService): BaseFragmentViewModel<BaseFragment<FragmentListBinding>,
        FragmentListBinding>(),ViewModelData<PixivisionResopnse> {

    var obsNewData : Observable<PixivisionResopnse>? = null
    lateinit var account: Account

    private var allowLoadMore = true
    private var errorIndex = 0
    private lateinit var adapter : PixivisionAdapter

    override fun runView() {
        getData(obsNewData)
    }

    override fun initViewModel() {
        val layoutManger = GridLayoutManager(mView.context,1)
        mBind.recyclerView.layoutManager = layoutManger
        adapter = PixivisionAdapter(mView.context)
        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(mView.context, R.color.primary))
        mBind.srLayout.setOnRefreshListener({ getData(obsNewData) })
        mBind.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onBottom() {
                if(allowLoadMore){
                    getMoreData()
                }
            }
            override fun scrollUp(dy: Int) {
                //
            }
            override fun scrollDown(dy: Int) {
                //
            }
        })
        mBind.recyclerView.adapter = adapter
    }

    override fun getData(obs: Observable<PixivisionResopnse>?) {
        if(obs != null){
            Observable.just(obs)
                    .doOnNext({ errorIndex = 0 })
                    .doOnNext({ if(obs != obsNewData) obsNewData = obs })
                    .doOnNext({ mBind.srLayout.isRefreshing = true })
                    .observeOn(Schedulers.io())
                    .flatMap({ obs -> obs })
                    .doOnNext({ response -> adapter.setNewData(response) })
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
                .flatMap({ token -> apiService.getPixivisionNext(token,adapter.nextUrl?:"")})
                .doOnNext({ response -> adapter.addMoreData(response) })
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