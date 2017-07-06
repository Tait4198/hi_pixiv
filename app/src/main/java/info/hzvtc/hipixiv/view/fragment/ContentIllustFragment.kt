package info.hzvtc.hipixiv.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.FragmentContentIllustBinding
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.illust.SingleIllust
import info.hzvtc.hipixiv.vm.fragment.ContentIllustViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


@SuppressLint("ValidFragment")
class ContentIllustFragment(val illustId : Int,val illust : Illust?) : BindingFragment<FragmentContentIllustBinding>(){

    @Inject
    lateinit var viewModel : ContentIllustViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun getLayoutId(): Int = R.layout.fragment_content_illust

    override fun initView(binding: FragmentContentIllustBinding): View {
        Observable.just(illustId)
                .flatMap {
                    if(illustId != 0){
                        return@flatMap viewModel.account.obsToken(this.context)
                                .flatMap { token -> viewModel.apiService.getIllust(token,illustId) }
                    }else{
                        return@flatMap Observable.just(SingleIllust(illust))
                    }
                }
                .filter{ (illust) -> illust != null}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    (illust) ->
                    if(illust != null){
                        viewModel.illust = illust
                        viewModel.setView(this)
                        viewModel.runView()
                    }
                },{
                    error->
                    Log.d("Error",error.message)
                    mBinding.msgLayout.visibility = View.VISIBLE
                    mBinding.progressBar.visibility = View.GONE
                },{
                    if(mBinding.progressBar.visibility == View.VISIBLE){
                        mBinding.msgLayout.visibility = View.VISIBLE
                        mBinding.progressBar.visibility = View.GONE
                    }
                })
        return binding.root
    }

}
