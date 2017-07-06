package info.hzvtc.hipixiv.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.pixivision.PixivisionResopnse
import info.hzvtc.hipixiv.vm.fragment.PixivisionViewModel
import io.reactivex.Observable
import javax.inject.Inject

@SuppressLint("ValidFragment")
class PixivisionLazyFragment(val obsNewData : Observable<PixivisionResopnse>, val account : Account)
    : LazyBindingFragment<FragmentListBinding>() {

    @Inject
    lateinit var viewModel : PixivisionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun onFirstUserVisible() {
        super.onFirstUserVisible()
        viewModel.obsNewData = obsNewData
        viewModel.account = account
        viewModel.setView(this)
        viewModel.runView()
    }
}