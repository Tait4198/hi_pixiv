package info.hzvtc.hipixiv.view.fragment

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.trend.TrendTagsResponse
import info.hzvtc.hipixiv.vm.fragment.TrendTagViewModel
import io.reactivex.Observable
import javax.inject.Inject

class TrendTagLazyFragment(val obsNewData : Observable<TrendTagsResponse>) :
        LazyBindingFragment<FragmentListBinding>() {

    @Inject
    lateinit var viewModel : TrendTagViewModel

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onFirstUserVisible() {
        super.onFirstUserVisible()
        viewModel.obsNewData = obsNewData
        viewModel.setView(this)
        viewModel.runView()
    }

}