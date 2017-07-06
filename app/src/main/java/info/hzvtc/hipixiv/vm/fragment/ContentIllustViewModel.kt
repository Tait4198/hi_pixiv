package info.hzvtc.hipixiv.vm.fragment

import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.ContentIllustAdapter
import info.hzvtc.hipixiv.adapter.events.ImageClick
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.FragmentContentIllustBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.view.fragment.BaseFragment
import io.reactivex.Observable
import javax.inject.Inject

class ContentIllustViewModel @Inject constructor(val account: Account, val apiService: ApiService, val userPreferences: UserPreferences)
    : BaseFragmentViewModel<BaseFragment<FragmentContentIllustBinding>, FragmentContentIllustBinding>(){

    lateinit var illust : Illust

    override fun runView() {
        Observable.just(userPreferences.isPremium?:false)
                .filter{ premium -> premium}
                .flatMap { account.obsToken(getContext()) }
                .flatMap { token -> apiService.postAddIllustBrowsingHistory(token,getIdList(illust.pixivId)) }
                .subscribe({
                    //
                },{
                    error -> Log.d("Error",error.toString())
                })
    }

    override fun initViewModel() {
        val metaAdapter = ContentIllustAdapter(getContext(), ContentIllustAdapter.Type.META)
        val contentAdapter = ContentIllustAdapter(getContext(), ContentIllustAdapter.Type.INFO)

        mBind.imageRecycler.layoutManager = LinearLayoutManager(getContext())
        mBind.imageRecycler.adapter = metaAdapter
        metaAdapter.setNewData(illust)
        metaAdapter.setImageClick(object : ImageClick{
            override fun imageClick(position: Int, urlsList: ArrayList<String>) {
                val intent = Intent(getString(R.string.activity_image))
                intent.putExtra(getString(R.string.extra_int),position)
                intent.putStringArrayListExtra(getString(R.string.extra_list),urlsList)
                ActivityCompat.startActivity(getContext(), intent, null)
            }
        })

        mBind.bottomRecycler.layoutManager = LinearLayoutManager(getContext())
        mBind.bottomRecycler.adapter = contentAdapter
        val behavior = BottomSheetBehavior.from(mBind.bottomRecycler)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                mBind.fabLike.translationY = slideOffset * (mBind.fabLike.height * 2.5f)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        contentAdapter.setNewData(illust)

        mBind.bottomRecycler.visibility = View.VISIBLE
        mBind.imageRecycler.visibility = View.VISIBLE
        mBind.fabLike.visibility = View.VISIBLE
        mBind.progressBar.visibility = View.GONE
    }

    private fun getIdList(illustId : Int) : List<Int>{
        val list = ArrayList<Int>()
        list.add(illustId)
        return list
    }
}
