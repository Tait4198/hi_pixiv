package info.hzvtc.hipixiv.vm

import android.support.v4.app.Fragment
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.databinding.ActivityRelatedBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.view.RelatedActivity
import info.hzvtc.hipixiv.view.fragment.IllustFragment
import javax.inject.Inject

class RelatedViewModel @Inject constructor(val account: Account,val apiService: ApiService)
    : BaseViewModel<RelatedActivity, ActivityRelatedBinding>() {


    override fun initViewModel() {
        val illustId = mView.intent.getIntExtra(getString(R.string.extra_int),0)
        val fragment = IllustFragment(account.obsToken(mView)
                .flatMap { token -> apiService.getIllustRecommended(token,illustId) },account,IllustAdapter.Type.ILLUST)
        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = mView.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.commit()
    }
}