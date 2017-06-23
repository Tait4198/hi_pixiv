package info.hzvtc.hipixiv.vm

import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.data.ViewPagerBundle
import info.hzvtc.hipixiv.databinding.ActivityMainBinding
import info.hzvtc.hipixiv.databinding.DialogSingleFilterBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.view.MainActivity
import info.hzvtc.hipixiv.view.fragment.*
import io.reactivex.Observable
import javax.inject.Inject


class MainViewModel @Inject constructor(val userPreferences: UserPreferences,val account: Account,val apiService: ApiService)
    : BaseViewModel<MainActivity,ActivityMainBinding>() {

    var nowIdentifier = -1

    //restricts index
    private var restrictPos = 0
    //all public private
    private lateinit var restricts: Array<out String>
    private lateinit var obsToken : Observable<String>
    private lateinit var newestFollowBundle : ViewPagerBundle<BaseFragment<*>>

    private lateinit var homeIllustFragment : IllustFragment
    private lateinit var homeMangaFragment : IllustFragment
    private lateinit var followVpFragment : ViewPagerFragment

    override fun initViewModel() {
        obsToken = account.obsToken(mView)
        restricts = mView.resources.getStringArray(R.array.restrict_parameters)
        newestFollowBundle = object : ViewPagerBundle<BaseFragment<*>>() {
            init {
                titles = mView.resources.getStringArray(R.array.newest_follow_tab)
                pagers = arrayOf(
                        IllustLazyFragment(obsToken.flatMap({ token -> apiService.getFollowIllusts(token,restricts[0])}),account,false),
                        IllustLazyFragment(obsToken.flatMap({ token -> apiService.getRecommendedIllusts(token,false)}),account,false),
                        EmptyFragment())
            }
            override fun fabClick() {
                when(nowPosition) {
                    0 -> {
                        showSingleFilterDialog(R.array.newest_follow_illust_items,object : Action{
                            override fun doAction() {
                                (pagers[0] as IllustLazyFragment).getViewModelData()?.getData(obsToken.
                                        flatMap({token -> apiService.getFollowIllusts(token,restricts[restrictPos])}))
                            }
                        })
                    }
                }
            }
            override fun fabShow(position: Int) {
                super.fabShow(position)
                if(position != 2)
                    mView.setFabVisible(true,true)
                else
                    mView.setFabVisible(false,false)
            }
        }

        followVpFragment = ViewPagerFragment(newestFollowBundle)
        homeIllustFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getRecommendedIllusts(token, true) }),account,false)
        homeMangaFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getRecommendedMangaList(token, true) }),account,true)
    }

    fun switchPage(identifier : Int){
        if(identifier != nowIdentifier){
            nowIdentifier = identifier
            userPreferences.pageIdentifier = identifier.toLong()
            when(identifier){
                //主页插画
                MainActivity.Identifier.HOME_ILLUSTRATIONS.value -> {
                    replaceFragment(homeIllustFragment)
                    mView.setFabVisible(false,false)
                }
                //主页漫画
                MainActivity.Identifier.HOME_MANGA.value -> {
                    replaceFragment(homeMangaFragment)
                    mView.setFabVisible(false,false)
                }
                //关注者
                MainActivity.Identifier.NEWEST_FOLLOW.value -> {
                    replaceFragment(followVpFragment)
                    mBind.fab.setImageDrawable(ContextCompat.getDrawable(mView,R.drawable.ic_filter))
                    mBind.fab.setOnClickListener({ newestFollowBundle.fabClick() })
                    mView.setFabVisible(true,true)
                }
            }
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = mView.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun showSingleFilterDialog(items: Int, action : Action){
        val dialog = MaterialDialog.Builder(mView)
                .title(mView.getString(R.string.newest_follow_illust_name))
                .customView(R.layout.dialog_single_filter, true)
                .positiveText(mView.getString(R.string.app_dialog_ok))
                .negativeText(mView.getString(R.string.app_dialog_cancel))
                .onPositive({ _, _ -> action.doAction()})
                .build()
        val bind = DataBindingUtil.bind<DialogSingleFilterBinding>(dialog.customView)
        bind.restrict.adapter = ArrayAdapter<String>(mView,android.R.layout.simple_list_item_1
                ,mView.resources.getStringArray(items))
        bind.restrict.setSelection(restrictPos)
        bind.restrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                restrictPos = position
            }
        }
        dialog.show()
    }

    interface Action{
        fun doAction()
    }
}
