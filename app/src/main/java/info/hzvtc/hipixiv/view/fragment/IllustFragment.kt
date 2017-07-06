package info.hzvtc.hipixiv.view.fragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.vm.fragment.IllustViewModel
import info.hzvtc.hipixiv.vm.fragment.ViewModelData
import io.reactivex.Observable
import javax.inject.Inject

@SuppressLint("ValidFragment")
class IllustFragment(val obsNewData : Observable<IllustResponse>, val account: Account, val contentType : IllustAdapter.Type)
    : BindingFragment<FragmentListBinding>() {

    @Inject
    lateinit var viewModel : IllustViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(binding: FragmentListBinding): View {
        viewModel.account = account
        viewModel.contentType = contentType
        viewModel.obsNewData = obsNewData
        viewModel.setView(this)
        viewModel.runView()
        return binding.root
    }
}
