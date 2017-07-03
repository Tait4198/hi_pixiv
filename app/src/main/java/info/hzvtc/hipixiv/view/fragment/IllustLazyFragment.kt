package info.hzvtc.hipixiv.view.fragment

import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.vm.fragment.IllustViewModel
import info.hzvtc.hipixiv.vm.fragment.ViewModelData
import io.reactivex.Observable
import javax.inject.Inject

class IllustLazyFragment(val obsNewData : Observable<IllustResponse>, val account : Account,val contentType : IllustAdapter.Type)
    : LazyBindingFragment<FragmentListBinding>(){

    @Inject
    lateinit var viewModel : IllustViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun onFirstUserVisible() {
        super.onFirstUserVisible()
        viewModel.obsNewData = obsNewData
        viewModel.contentType = contentType
        viewModel.account = account
        viewModel.setView(this)
        viewModel.runView()
    }
}