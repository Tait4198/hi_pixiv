package info.hzvtc.hipixiv.vm

import android.content.ContentUris
import android.net.Uri
import android.support.v4.app.Fragment
import com.google.gson.Gson
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityContentBinding
import info.hzvtc.hipixiv.pojo.illust.Illust
import info.hzvtc.hipixiv.pojo.user.UserPreview
import info.hzvtc.hipixiv.view.ContentActivity
import info.hzvtc.hipixiv.view.fragment.ContentIllustFragment
import info.hzvtc.hipixiv.view.fragment.ContentUserFragment
import javax.inject.Inject

class ContentViewModel @Inject constructor(val gson: Gson) : BaseViewModel<ContentActivity, ActivityContentBinding>() {

    override fun initViewModel() {
        val type = mView.intent.getStringExtra(getString(R.string.extra_type))
        val uri : Uri? = mView.intent.data
        val host = if(uri != null) uri.host else ""
        val data = if(uri != null) ContentUris.parseId(uri).toInt() else 0

        if(type == getString(R.string.extra_type_illust) || host == getString(R.string.host_pixiv)){
            mBind.toolbar.title = getString(R.string.content_illust)
            val illustId = mView.intent.getIntExtra(getString(R.string.extra_int),data)

            if(illustId != 0){
                showByIllust(illustId,null)
            }else{
                val json = mView.intent.getStringExtra(getString(R.string.extra_json))
                if(json != null){
                    val illust : Illust = gson.fromJson(json,Illust::class.java)
                    showByIllust(0,illust)
                }else{
                    showByIllust(0,null)
                }
            }
        }else if(type == getString(R.string.extra_type_user) || host == getString(R.string.host_user)){
            mBind.toolbar.title = getString(R.string.content_user)
            val userId = mView.intent.getIntExtra(getString(R.string.extra_int),data)
            showByUserPreview(userId)
        }
    }

    private fun showByIllust(illustId : Int, illust : Illust?){
        replaceFragment(ContentIllustFragment(illustId,illust))
    }

    private fun showByUserPreview(userId : Int){
        replaceFragment(ContentUserFragment(userId))
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = mView.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.commit()
    }
}