package info.hzvtc.hipixiv.vm.fragment

import com.google.gson.Gson
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.FragmentContentUserBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import javax.inject.Inject

class ContentUserViewModel @Inject constructor(val account: Account, val apiService: ApiService,
                                               private val userPreferences: UserPreferences, val gson: Gson) :
        BaseFragmentViewModel<BaseFragment<FragmentContentUserBinding>, FragmentContentUserBinding>(){

    override fun runView() {

    }

    override fun initViewModel() {

    }

}