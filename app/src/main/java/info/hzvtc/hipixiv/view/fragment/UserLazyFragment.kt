package info.hzvtc.hipixiv.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.user.UserResponse
import info.hzvtc.hipixiv.vm.fragment.UserViewModel
import info.hzvtc.hipixiv.vm.fragment.ViewModelData
import io.reactivex.Observable
import javax.inject.Inject

@SuppressLint("ValidFragment")
class UserLazyFragment(val obsNewData : Observable<UserResponse>, val account : Account)
    : LazyBindingFragment<FragmentListBinding>() {

    @Inject
    lateinit var viewModel : UserViewModel

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onFirstUserVisible() {
        super.onFirstUserVisible()
        viewModel.account = account
        viewModel.obsNewData = obsNewData
        viewModel.setView(this)
        viewModel.runView()
    }
}