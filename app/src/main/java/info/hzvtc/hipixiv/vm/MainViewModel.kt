package info.hzvtc.hipixiv.vm

import android.databinding.DataBindingUtil
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.BookmarkTagAdapter
import info.hzvtc.hipixiv.adapter.IllustAdapter
import info.hzvtc.hipixiv.adapter.SimplePagerAdapter
import info.hzvtc.hipixiv.adapter.events.TagItemClick
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.data.ViewPagerBundle
import info.hzvtc.hipixiv.databinding.*
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.MainActivity
import info.hzvtc.hipixiv.view.fragment.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainViewModel @Inject constructor(val userPreferences: UserPreferences,val account: Account,val apiService: ApiService)
    : BaseViewModel<MainActivity,ActivityMainBinding>() {

    private var nowIdentifier = -1

    //关注者
    //restricts position
    private var restrictPos = 0
    //collect restrict position
    private var collectRestrictPos = 0
    //item[3]-> all public private
    private lateinit var restricts: Array<out String>

    //标签过滤
    //item[2]-> public private
    private lateinit var doubleRestricts: Array<out String>
    private var lastPosition = 0
    private var isPublicPage = true
    private var dialog : MaterialDialog? = null

    private lateinit var obsToken : Observable<String>
    private lateinit var newestFollowBundle : ViewPagerBundle<BaseFragment<*>>
    private lateinit var newestNewBundle : ViewPagerBundle<BaseFragment<*>>
    private lateinit var userPageBundle : ViewPagerBundle<BaseFragment<*>>
    private lateinit var pixivisionPageBundle : ViewPagerBundle<BaseFragment<*>>

    private lateinit var homeIllustFragment : IllustFragment
    private lateinit var homeMangaFragment : IllustFragment
    private lateinit var followVpFragment : ViewPagerFragment
    private lateinit var newVpFragment : ViewPagerFragment
    private lateinit var pixivisionVpFragment : ViewPagerFragment
    private lateinit var myPixivFragment : IllustFragment
    private lateinit var collectFragment : IllustFragment
    private lateinit var historyFragment : IllustFragment
    private lateinit var userVpFragment : ViewPagerFragment

    override fun initViewModel() {
        obsToken = account.obsToken(mView)
        restricts = mView.resources.getStringArray(R.array.restrict_parameters)
        doubleRestricts = mView.resources.getStringArray(R.array.double_restrict_parameters)
        //newest -> follow -> bundle
        newestFollowBundle = object : ViewPagerBundle<BaseFragment<*>>() {
            init {
                titles = mView.resources.getStringArray(R.array.newest_follow_tab)
                pagers = arrayOf(
                        IllustLazyFragment(obsToken.flatMap({ token -> apiService.getFollowIllusts(token,restricts[0])}),account,IllustAdapter.Type.ILLUST),
                        UserLazyFragment(obsToken.flatMap({ token -> apiService.getUserRecommended(token)}),account))
            }
            override fun fabClick() {
                when(nowPosition) {
                    0 -> {
                        showSingleFilterDialog(mView.resources.getStringArray(R.array.newest_follow_illust_items),object : Action{
                            override fun doAction() {
                                (pagers[0] as IllustLazyFragment).viewModel.getData(obsToken.
                                        flatMap({token -> apiService.getFollowIllusts(token,restricts[restrictPos])}))
                            }
                        })
                    }
                }
            }
            override fun fabShow(position: Int) {
                super.fabShow(position)
                if(position != 1)
                    mView.setFabVisible(true,true)
                else
                    mView.setFabVisible(false,false)
            }
        }

        //newest -> new -> bundle
        newestNewBundle = object : ViewPagerBundle<BaseFragment<*>>(){
            init {
                titles = mView.resources.getStringArray(R.array.newest_new_tab)
                pagers = arrayOf(
                        IllustLazyFragment(obsToken.flatMap({ token -> apiService.getNewIllust(token,"illust")}),account,IllustAdapter.Type.ILLUST),
                        IllustLazyFragment(obsToken.flatMap({ token -> apiService.getNewIllust(token,"manga")}),account,IllustAdapter.Type.MANGA))
            }
        }
        //user
        userPageBundle = object : ViewPagerBundle<BaseFragment<*>>(){
            init {
                titles = mView.resources.getStringArray(R.array.user_tab)
                pagers = arrayOf(
                        UserLazyFragment(obsToken.flatMap({ token ->
                            apiService.getUserFollowing(token,userPreferences.id?:0,doubleRestricts[0])}),account),
                        UserLazyFragment(obsToken.flatMap({ token ->
                            apiService.getUserFollower(token,userPreferences.id?:0)}),account),
                        UserLazyFragment(obsToken.flatMap({ token ->
                            apiService.getUserMyPixiv(token,userPreferences.id?:0)}),account)
                )
            }

            override fun fabClick() {
                super.fabClick()
                when(nowPosition){
                    0 ->{
                        showSingleFilterDialog(mView.resources.getStringArray(R.array.filter_items),object : Action{
                            override fun doAction() {
                                (pagers[0] as UserLazyFragment).viewModel.getData(obsToken.
                                        flatMap({token -> apiService.getUserFollowing(token,userPreferences.id?:0,doubleRestricts[restrictPos])}))
                            }
                        })
                    }
                }
            }

            override fun fabShow(position: Int) {
                super.fabShow(position)
                if(position == 0)
                    mView.setFabVisible(true,true)
                else
                    mView.setFabVisible(false,false)
            }
        }
        //Pixivision
        pixivisionPageBundle = object : ViewPagerBundle<BaseFragment<*>>(){
            init {
                titles = mView.resources.getStringArray(R.array.newest_new_tab)
                pagers = arrayOf(
                        PixivisionLazyFragment(obsToken.flatMap({ token -> apiService.getPixivisionArticles(token,"illust")}),account),
                        PixivisionLazyFragment(obsToken.flatMap({ token -> apiService.getPixivisionArticles(token,"manga")}),account)
                )
            }
        }


        homeIllustFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getRecommendedIllusts(token, true) }),account,IllustAdapter.Type.ILLUST)
        homeMangaFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getRecommendedMangaList(token, true) }),account,IllustAdapter.Type.MANGA)
        followVpFragment = ViewPagerFragment(newestFollowBundle)
        newVpFragment = ViewPagerFragment(newestNewBundle)
        pixivisionVpFragment = ViewPagerFragment(pixivisionPageBundle)
        myPixivFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getMyPixivIllusts(token)}),account,IllustAdapter.Type.ILLUST)
        collectFragment = IllustFragment(obsToken.flatMap({ token -> apiService
                .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[0])}),account,IllustAdapter.Type.ILLUST)
        historyFragment = IllustFragment(obsToken.flatMap({ token -> apiService.getIllustBrowsingHistory(token)}),account,IllustAdapter.Type.ILLUST)
        userVpFragment = ViewPagerFragment(userPageBundle)
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
                    restrictPos = 0
                    mBind.fab.setImageDrawable(ContextCompat.getDrawable(mView,R.drawable.ic_filter))
                    mBind.fab.setOnClickListener({ newestFollowBundle.fabClick() })
                    mView.setFabVisible(true,true)
                }
                //最新
                MainActivity.Identifier.NEWEST_NEW.value -> {
                    replaceFragment(newVpFragment)
                    mView.setFabVisible(false,false)
                }
                //Pixivision
                MainActivity.Identifier.PIXIVISION.value ->{
                    replaceFragment(pixivisionVpFragment)
                    mView.setFabVisible(false,false)
                }
                //My Pixiv
                MainActivity.Identifier.NEWEST_MY_PIXIV.value ->{
                    replaceFragment(myPixivFragment)
                    mView.setFabVisible(false,false)
                }
                //收集
                MainActivity.Identifier.COLLECT.value ->{
                    replaceFragment(collectFragment)
                    lastPosition = 0
                    isPublicPage = true
                    dialog = null
                    mBind.fab.setImageDrawable(ContextCompat.getDrawable(mView,R.drawable.ic_filter))
                    mBind.fab.setOnClickListener({ showTagViewPagerDialog()})
                    mView.setFabVisible(true,true)
                }
                //浏览历史
                MainActivity.Identifier.BROWSING_HISTORY.value ->{
                    replaceFragment(historyFragment)
                    mView.setFabVisible(false,false)
                }
                //用户
                MainActivity.Identifier.USER.value ->{
                    replaceFragment(userVpFragment)
                    restrictPos = 0
                    mBind.fab.setImageDrawable(ContextCompat.getDrawable(mView,R.drawable.ic_filter))
                    mBind.fab.setOnClickListener({ userPageBundle.fabClick() })
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

    private fun showSingleFilterDialog(items: Array<String>, action : Action){
        val dialog = MaterialDialog.Builder(mView)
                .title(getString(R.string.newest_follow_illust_name))
                .customView(R.layout.dialog_single_filter, true)
                .positiveText(getString(R.string.app_dialog_ok))
                .negativeText(getString(R.string.app_dialog_cancel))
                .onPositive({ _, _ -> action.doAction()})
                .build()
        val bind = DataBindingUtil.bind<DialogSingleFilterBinding>(dialog.customView)
        bind.restrict.adapter = ArrayAdapter<String>(mView,android.R.layout.simple_list_item_1,items)
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

    //标签过滤
    private fun showTagViewPagerDialog(){
        if(dialog == null){
            dialog = MaterialDialog.Builder(mView)
                    .title(getString(R.string.collect_filter_name))
                    .negativeText(getString(R.string.app_dialog_cancel))
                    .customView(R.layout.dialog_view_pager, false)
                    .build()
            val bind = DataBindingUtil.bind<DialogViewPagerBinding>(dialog?.customView)
            val publicPage = DataBindingUtil.bind<FragmentListBinding>(View.inflate(mView,R.layout.fragment_list,null))
            val privatePage = DataBindingUtil.bind<FragmentListBinding>(View.inflate(mView,R.layout.fragment_list,null))
            val adapter = SimplePagerAdapter(arrayOf(publicPage.root,privatePage.root),
                    mView.resources.getStringArray(R.array.filter_items))
            val publicPageAdapter = BookmarkTagAdapter(mView)
            val privatePageAdapter = BookmarkTagAdapter(mView)
            publicPage.recyclerView.adapter = publicPageAdapter
            publicPage.recyclerView.layoutManager = LinearLayoutManager(mView)
            publicPage.srLayout.setColorSchemeColors(ContextCompat.getColor(mView, R.color.primary))
            publicPage.srLayout.setOnRefreshListener({ initPageData(publicPage,publicPageAdapter,true) })
            privatePage.recyclerView.adapter = privatePageAdapter
            privatePage.recyclerView.layoutManager = LinearLayoutManager(mView)
            privatePage.srLayout.setColorSchemeColors(ContextCompat.getColor(mView, R.color.primary))
            privatePage.srLayout.setOnRefreshListener({ initPageData(privatePage,privatePageAdapter,false) })
            publicPageAdapter.setTagItemClick(object : TagItemClick {
                override fun itemClick(position: Int,tag : String) {
                    if(!isPublicPage){
                        privatePageAdapter.updateLastPositionItem(lastPosition)
                    }else{
                        publicPageAdapter.updateLastPositionItem(lastPosition)
                    }
                    publicPageAdapter.updatePositionItem(position)
                    lastPosition = position
                    isPublicPage = true
                    when(position){
                        0 ->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[0])}))
                        }
                        1 ->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[0],"未分類")}))
                        }
                        else->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[0],tag)}))
                        }
                    }
                    dialog?.hide()
                }
            })
            privatePageAdapter.setTagItemClick(object : TagItemClick {
                override fun itemClick(position: Int,tag : String) {
                    if(isPublicPage){
                        publicPageAdapter.updateLastPositionItem(lastPosition)
                    }else {
                        privatePageAdapter.updateLastPositionItem(lastPosition)
                    }
                    privatePageAdapter.updatePositionItem(position)
                    when(position){
                        0 ->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[1])}))
                        }
                        1 ->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[1],"未分類")}))
                        }
                        else->{
                            collectFragment.viewModel.getData(obsToken.flatMap({ token -> apiService
                                    .getLikeIllust(token,userPreferences.id?:0,doubleRestricts[1],tag)}))
                        }
                    }
                    lastPosition = position
                    isPublicPage = false
                    dialog?.hide()
                }
            })

            bind.viewPager.adapter = adapter
            bind.viewPager.currentItem = collectRestrictPos
            bind.tab.setupWithViewPager(bind.viewPager)
            bind.tab.tabMode = TabLayout.MODE_FIXED

            initPageData(publicPage,publicPageAdapter,true)
            initPageData(privatePage,privatePageAdapter,false)
        }
        dialog?.show()
    }

    //加载标签选择页面数据
    private fun initPageData(page : FragmentListBinding,pageAdapter : BookmarkTagAdapter?,isPublic : Boolean){
        val restrict = if(isPublic) doubleRestricts[0] else doubleRestricts[1]
        val mPos = if(isPublic){ if(isPublicPage) lastPosition else -1 }else{ if(isPublicPage) -1 else lastPosition }

        val isNetConnected = AppUtil.isNetworkConnected(mView)
        Observable.just(isNetConnected)
                .filter({connected -> connected})
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ page.srLayout.isRefreshing = true})
                .observeOn(Schedulers.io())
                .flatMap({ account.obsToken(mView) })
                .flatMap({
                    token -> apiService.getIllustBookmarkTags(token,userPreferences.id?:0,restrict)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response -> pageAdapter?.initNewData(response,mPos)
                },{
                    error -> Log.d("Error",error.printStackTrace().toString())
                    page.srLayout.isRefreshing = false
                },{
                    if(!isNetConnected) pageAdapter?.initNewData(null,mPos)
                     page.srLayout.isRefreshing = false
                })
    }

    interface Action{
        fun doAction()
    }
}