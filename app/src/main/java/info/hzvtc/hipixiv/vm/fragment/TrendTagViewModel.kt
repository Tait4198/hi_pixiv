package info.hzvtc.hipixiv.vm.fragment

import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.TrendTagItemClick
import info.hzvtc.hipixiv.adapter.TrendTagsAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.trend.TrendTagsResponse
import info.hzvtc.hipixiv.view.SearchActivity
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TrendTagViewModel @Inject constructor():
        BaseFragmentViewModel<BaseFragment<FragmentListBinding>,FragmentListBinding>(),ViewModelData<TrendTagsResponse> {

    var obsNewData : Observable<TrendTagsResponse>? = null

    private lateinit var adapter: TrendTagsAdapter

    override fun runView() {
        getData(obsNewData)
    }

    override fun initViewModel() {
        val layoutManger = GridLayoutManager(mView.context,3)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(pos: Int): Int = if(adapter.getFull(pos)) 1 else layoutManger.spanCount
        }
        mBind.recyclerView.layoutManager = layoutManger
        adapter = TrendTagsAdapter(mView.context)
        adapter.setTrendTagItemClick(object : TrendTagItemClick{
            override fun itemClick(tag: String) {
                if(mView.activity is SearchActivity){
                    (mView.activity as SearchActivity).searchByTrendTag(tag)
                }
            }
        })
        mBind.srLayout.setColorSchemeColors(ContextCompat.getColor(mView.context, R.color.primary))
        mBind.srLayout.setOnRefreshListener({ getData(obsNewData) })
        mBind.recyclerView.adapter = adapter
    }

    override fun getData(obs: Observable<TrendTagsResponse>?) {
        if(obs != null){
            Observable.just(obs)
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
                        Log.d("Error",error.toString())
                        mBind.srLayout.isRefreshing = false
                    },{
                        mBind.srLayout.isRefreshing = false
                    })
        }
    }

    override fun getMoreData() {
        //
    }
}