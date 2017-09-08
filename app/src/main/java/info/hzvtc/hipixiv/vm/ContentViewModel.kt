package info.hzvtc.hipixiv.vm

import android.content.ContentUris
import android.support.v4.app.Fragment
import com.google.gson.Gson
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityContentBinding
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.user.UserPreview
import info.hzvtc.hipixiv.view.ContentActivity
import info.hzvtc.hipixiv.view.fragment.ContentIllustFragment
import javax.inject.Inject

class ContentViewModel @Inject constructor(val gson: Gson) : BaseViewModel<ContentActivity, ActivityContentBinding>() {

    override fun initViewModel() {
        val type = mView.intent.getStringExtra(getString(R.string.extra_type))

        if(type == getString(R.string.extra_type_illust) || mView.intent.data != null){
            mBind.toolbar.title = getString(R.string.content_illust)
            var illustId = mView.intent.getIntExtra(getString(R.string.extra_int),0)
            if(illustId == 0 && mView.intent.data != null){
                illustId = ContentUris.parseId(mView.intent.data).toInt()
            }

            if(illustId != 0){
                showByIllust(illustId,null)
            }else{
                val json = mView.intent.getStringExtra(getString(R.string.extra_json))
                if(json != null){
                    val illust : Illust = gson.fromJson(json,Illust::class.java)
                    mView.viewModel.showByIllust(0,illust)
                }else{
                    mView.viewModel.showByIllust(0,null)
                }
            }
        }else if(type == getString(R.string.extra_type_user)){

        }
    }

    private fun showByIllust(illustId : Int, illust : Illust?){
        replaceFragment(ContentIllustFragment(illustId,illust))
    }

    fun showByUserPreview(userId : Int,userPreview: UserPreview){
        //
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = mView.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.commit()
    }
}