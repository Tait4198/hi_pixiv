package info.hzvtc.hipixiv.view

import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityMainBinding
import info.hzvtc.hipixiv.vm.MainViewModel
import javax.inject.Inject
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import info.hzvtc.hipixiv.data.UserPreferences


class MainActivity : BindingActivity<ActivityMainBinding>() {

    @Inject
    lateinit var viewModel : MainViewModel
    @Inject
    lateinit var userPref : UserPreferences

    private var isShowFab : Boolean = false

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)

        mBinding.layoutToolbar.toolbar.setTitle(R.string.app_name)
        setSupportActionBar(mBinding.layoutToolbar.toolbar)

        if(userPref.pageIdentifier == 0L) userPref.pageIdentifier = Identifier.HOME_ILLUSTRATIONS.value.toLong()
        val tempIdentifier = userPref.pageIdentifier?:Identifier.HOME_ILLUSTRATIONS.value.toLong()
//        val homeExpanded = tempIdentifier >= Identifier.HOME_ILLUSTRATIONS.value && tempIdentifier <= Identifier.HOME_NOVEL.value
        val homeExpanded = tempIdentifier >= Identifier.HOME_ILLUSTRATIONS.value && tempIdentifier <= Identifier.HOME_MANGA.value
        val newestExpanded = tempIdentifier >= Identifier.NEWEST_FOLLOW.value && tempIdentifier <= Identifier.NEWEST_MY_PIXIV.value
        val dividerItem = DividerDrawerItem()
        val drawer = DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.layout_header)
                .withToolbar(mBinding.layoutToolbar.toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withMultiSelect(false)
                .addDrawerItems(
                        ExpandableDrawerItem().withName(R.string.home_name).withIcon(GoogleMaterial.Icon.gmd_home)
                                .withSelectable(false).withIsExpanded(homeExpanded).withSubItems(
                                PrimaryDrawerItem().withName(R.string.home_illust).
                                        withIdentifier(Identifier.HOME_ILLUSTRATIONS.value.toLong()).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.home_mange).
                                        withIdentifier(Identifier.HOME_MANGA.value.toLong()).withLevel(2)
//                                PrimaryDrawerItem().withName(R.string.home_novel).
//                                        withIdentifier(Identifier.HOME_NOVEL.value.toLong()).withLevel(2)
                        ),
                        ExpandableDrawerItem().withName(R.string.newest_name).withIcon(GoogleMaterial.Icon.gmd_fiber_new)
                                .withSelectable(false).withIsExpanded(newestExpanded).withSubItems(
                                PrimaryDrawerItem().withName(R.string.newest_follow).
                                        withIdentifier(Identifier.NEWEST_FOLLOW.value.toLong()).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.newest_name).
                                        withIdentifier(Identifier.NEWEST_NEW.value.toLong()).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.newest_my_pixiv).
                                        withIdentifier(Identifier.NEWEST_MY_PIXIV.value.toLong()).withLevel(2)
                        ),
                        PrimaryDrawerItem().withName(R.string.pixivision_name).withIcon(GoogleMaterial.Icon.gmd_highlight)
                                .withIdentifier(Identifier.PIXIVISION.value.toLong()),
                        dividerItem,
                        PrimaryDrawerItem().withName(R.string.collect_name).withIcon(GoogleMaterial.Icon.gmd_favorite)
                                .withIdentifier(Identifier.COLLECT.value.toLong()),
                        PrimaryDrawerItem().withName(R.string.browsing_name).withIcon(GoogleMaterial.Icon.gmd_history)
                                .withIdentifier(Identifier.BROWSING_HISTORY.value.toLong())
                                .withEnabled(userPref.isPremium?:false),
                        PrimaryDrawerItem().withName(R.string.user_name).withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(Identifier.USER.value.toLong()),
                        dividerItem,
                        PrimaryDrawerItem().withName(R.string.setting_name).withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(Identifier.SETTING.value.toLong()).withSelectable(false)
                )
                .withOnDrawerItemClickListener({
                    _,_,drawerItem->
                    if (drawerItem.identifier < Identifier.SETTING.value && drawerItem is PrimaryDrawerItem) {
                        mBinding.layoutToolbar.toolbar.title = getTitle(drawerItem.identifier.toInt())
                        viewModel.switchPage(drawerItem.identifier.toInt())
                    }
                    false
                })
                .build()
        initDrawerHeader(drawer)
        drawer.setSelection(tempIdentifier)

        Log.d("Main",userPref.accessToken.toString())
        Log.d("Main",userPref.expires.toString())
    }

    private fun initDrawerHeader(drawer : Drawer){
        val profile = drawer.header.findViewById(R.id.user_profile) as SimpleDraweeView
        val account = drawer.header.findViewById(R.id.account) as TextView
        val member = drawer.header.findViewById(R.id.member) as TextView
        profile.hierarchy.roundingParams = RoundingParams.asCircle()
        profile.setImageURI(userPref.profileUrlLarge)
        account.text = userPref.account
        if(userPref.isPremium?:false){
            member.text = getString(R.string.senior_member)
            member.setTextColor(ContextCompat.getColor(this,R.color.md_yellow_200))
        }else{
            member.text = getString(R.string.general_user)
            member.setTextColor(ContextCompat.getColor(this,R.color.md_yellow_500))
        }
    }

    /**
     * @param status 当前是否显示
     * @param isShow 本次页面是否显示
     * */
    fun setFabVisible(status : Boolean,isShow : Boolean){
        isShowFab = isShow
        if(status) mBinding.fab.show() else mBinding.fab.hide()
    }

    fun showFab(status : Boolean){
        if(isShowFab){
            if(status) mBinding.fab.show() else mBinding.fab.hide()
        }
    }


    fun getRootView(): CoordinatorLayout? {
        return mBinding.rootView
    }

    private fun getTitle(identifier : Int) : String{
        var title = ""
        when(identifier){
            Identifier.HOME_ILLUSTRATIONS.value -> title = getString(R.string.home_illust)
            Identifier.HOME_MANGA.value -> title = getString(R.string.home_mange)
            //Identifier.HOME_NOVEL.value -> title = getString(R.string.home_novel)
            Identifier.NEWEST_FOLLOW.value -> title = getString(R.string.newest_follow)
            Identifier.NEWEST_NEW.value -> title = getString(R.string.newest_new)
            Identifier.NEWEST_MY_PIXIV.value -> title = getString(R.string.newest_my_pixiv)
            Identifier.PIXIVISION.value -> title = getString(R.string.pixivision_name)
            Identifier.COLLECT.value -> title = getString(R.string.collect_name)
            Identifier.BROWSING_HISTORY.value -> title = getString(R.string.browsing_name)
            Identifier.USER.value -> title = getString(R.string.user_name)
        }
        return title
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    enum class Identifier(val value : Int){
        HOME_ILLUSTRATIONS(101),
        HOME_MANGA(102),
        //HOME_NOVEL(103),
        NEWEST_FOLLOW(201),
        NEWEST_NEW(202),
        NEWEST_MY_PIXIV(203),
        PIXIVISION(301),
        COLLECT(401),
        BROWSING_HISTORY(501),
        USER(601),
        SETTING(701);
    }
}