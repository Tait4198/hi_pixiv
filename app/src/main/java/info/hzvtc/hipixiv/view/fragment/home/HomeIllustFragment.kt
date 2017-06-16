package info.hzvtc.hipixiv.view.fragment.home

import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentHomeIllustBinding
import info.hzvtc.hipixiv.view.fragment.BindingFragment

class HomeIllustFragment : BindingFragment<FragmentHomeIllustBinding>() {


    override fun getLayoutId(): Int = R.layout.fragment_home_illust

    override fun initView(binding: FragmentHomeIllustBinding): View {
        component.inject(this)
        return binding.root
    }
}
