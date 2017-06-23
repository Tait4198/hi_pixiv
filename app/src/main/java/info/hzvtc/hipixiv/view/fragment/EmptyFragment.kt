package info.hzvtc.hipixiv.view.fragment

import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentEmptyBinding
import info.hzvtc.hipixiv.vm.fragment.ViewModelData

class EmptyFragment : LazyBindingFragment<FragmentEmptyBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_empty

    override fun onFirstUserVisible() {

    }

    override fun getViewModelData(): ViewModelData<*>? {
        return null
    }

}