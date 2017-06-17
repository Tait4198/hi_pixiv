package info.hzvtc.hipixiv.view.fragment

import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.databinding.FragmentIllustBinding
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.vm.fragment.IllustViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class IllustFragment(val obs : Observable<IllustResponse>) : BindingFragment<FragmentIllustBinding>() {

    @Inject
    lateinit var viewModel : IllustViewModel

    lateinit var layoutManger : RecyclerView.LayoutManager
    lateinit var adapter : IllustAdapter

    override fun getLayoutId(): Int = R.layout.fragment_illust

    override fun initView(binding: FragmentIllustBinding): View {
        component.inject(this)
        viewModel.setView(this)

        mBinding.srLayout.setColorSchemeColors(ContextCompat.getColor(this.context,R.color.primary))
        mBinding.srLayout.setOnRefreshListener({ newData() })
        mBinding.illustRecycler.itemAnimator = DefaultItemAnimator()

        layoutManger = GridLayoutManager(this.context,2) as RecyclerView.LayoutManager
        adapter = IllustAdapter(this.context)

        newData()

        return binding.root
    }

    fun newData(){
        Observable.just(obs)
             .doOnNext({ mBinding.srLayout.isRefreshing = true })
             .flatMap({ obs -> obs })
             .doOnNext({ illustResponse -> adapter.setNewData(illustResponse) })
             .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _ ->
                        mBinding.illustRecycler.adapter = adapter
                        mBinding.illustRecycler.layoutManager = layoutManger
                    },{
                        error -> Log.d("Error",error.toString())
                    },{
                        mBinding.srLayout.isRefreshing = false
                    })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}
