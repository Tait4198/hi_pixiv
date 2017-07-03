package info.hzvtc.hipixiv.view.fragment

import android.os.Bundle
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.FragmentListBinding
import info.hzvtc.hipixiv.pojo.user.UserResponse
import info.hzvtc.hipixiv.vm.fragment.UserViewModel
import io.reactivex.Observable
import javax.inject.Inject

class UserSearchFragment(val account: Account) : BindingFragment<FragmentListBinding>(){


    @Inject
    lateinit var viewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(binding: FragmentListBinding): View {
        viewModel.account = account
        viewModel.setView(this)
        viewModel.runView()
        return binding.root
    }

    fun setTempObs(obs : Observable<UserResponse>?){
        viewModel.obsNewData = obs
        viewModel.runView()
    }
}