package info.hzvtc.hipixiv.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentContentUserBinding
import info.hzvtc.hipixiv.util.AppMessage
import info.hzvtc.hipixiv.vm.fragment.ContentUserViewModel
import javax.inject.Inject

@SuppressLint("ValidFragment")
class ContentUserFragment(private val userId : Int) : BindingFragment<FragmentContentUserBinding>() {

    @Inject
    lateinit var viewModel : ContentUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_content_user

    override fun initView(binding: FragmentContentUserBinding): View {
        AppMessage.toastMessageLong(userId.toString(),this.context)
        return binding.root
    }
}