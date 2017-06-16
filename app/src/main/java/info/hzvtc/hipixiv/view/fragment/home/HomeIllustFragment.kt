package info.hzvtc.hipixiv.view.fragment.home

import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentHomeIllustBinding
import info.hzvtc.hipixiv.view.fragment.BindingFragment
import info.hzvtc.hipixiv.vm.fragment.home.HomeIllustViewModel
import javax.inject.Inject

class HomeIllustFragment : BindingFragment<FragmentHomeIllustBinding>() {

    @Inject
    lateinit var viewModel : HomeIllustViewModel

    override fun getLayoutId(): Int = R.layout.fragment_home_illust

    override fun initView(binding: FragmentHomeIllustBinding): View {
        component.inject(this)
        viewModel.setView(this)
        return binding.root
    }
}
