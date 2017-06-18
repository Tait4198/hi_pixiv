package info.hzvtc.hipixiv.view.fragment

import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentIllustBinding
import info.hzvtc.hipixiv.pojo.illust.IllustResponse
import info.hzvtc.hipixiv.vm.fragment.IllustViewModel
import io.reactivex.Observable
import javax.inject.Inject

class IllustFragment(val obs : Observable<IllustResponse>) : BindingFragment<FragmentIllustBinding>() {

    @Inject
    lateinit var viewModel : IllustViewModel

    override fun getLayoutId(): Int = R.layout.fragment_illust

    override fun initView(binding: FragmentIllustBinding): View {
        component.inject(this)
        viewModel.obsNewData = obs
        viewModel.setView(this)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}
