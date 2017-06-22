package info.hzvtc.hipixiv.view.fragment

import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.vm.fragment.IllustViewModel
import info.hzvtc.hipixiv.vm.fragment.ViewModelData
import io.reactivex.Observable
import javax.inject.Inject

class IllustFragment(val obsNewData : Observable<IllustResponse>,val account : Account,val isManga: Boolean)
    : BindingFragment<FragmentListBinding>() {


    @Inject
    lateinit var viewModel : IllustViewModel

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(binding: FragmentListBinding): View {
        component.inject(this)
        viewModel.isManga = isManga
        viewModel.obsNewData = obsNewData
        viewModel.account = account
        viewModel.setView(this)
        return binding.root
    }

    override fun getViewModelData(): ViewModelData<IllustResponse>? {
        return viewModel
    }
}
