package info.hzvtc.hipixiv.view

import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityMainBinding
import info.hzvtc.hipixiv.vm.MainViewModel
import javax.inject.Inject
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.view.fragment.IllustFragment


class MainActivity : BindingActivity<ActivityMainBinding>() {

    @Inject
    lateinit var viewModel : MainViewModel
    @Inject
    lateinit var account : Account
    @Inject
    lateinit var userPref : UserPreferences
    @Inject
    lateinit var apiService : ApiService

    private var nowIdentifier = Identifier.HOME_ILLUSTRATIONS.value
    private var isFirst = true

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        viewModel.setView(this)

        mBinding.layoutToolbar.toolbar.setTitle(R.string.app_name)
        setSupportActionBar(mBinding.layoutToolbar.toolbar)

        if(savedInstanceState != null) nowIdentifier = savedInstanceState.getInt("Identifier")

        val dividerItem = DividerDrawerItem()
        val drawer = DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.layout_header)
                .withToolbar(mBinding.layoutToolbar.toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withMultiSelect(false)
                .addDrawerItems(
                        ExpandableDrawerItem().withName(R.string.home_name).withIcon(GoogleMaterial.Icon.gmd_home)
                                .withSelectable(false).withIsExpanded(true).withSubItems(
                                PrimaryDrawerItem().withName(R.string.home_illust).
                                        withIdentifier(Identifier.HOME_ILLUSTRATIONS.value.toLong()).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.home_mange).
                                        withIdentifier(Identifier.HOME_MANGA.value.toLong()).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.home_novel).
                                        withIdentifier(Identifier.HOME_NOVEL.value.toLong()).withLevel(2)
                        ),
                        ExpandableDrawerItem().withName(R.string.newest_name).withIcon(GoogleMaterial.Icon.gmd_fiber_new)
                                .withSelectable(false).withSubItems(
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
                                .withIdentifier(Identifier.COLLECT.value.toLong()).withSelectable(false),
                        PrimaryDrawerItem().withName(R.string.browsing_name).withIcon(GoogleMaterial.Icon.gmd_history)
                                .withIdentifier(Identifier.BROWSING_HISTORY.value.toLong()).withSelectable(false),
                        PrimaryDrawerItem().withName(R.string.user_name).withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(Identifier.USER.value.toLong()).withSelectable(false),
                        dividerItem,
                        PrimaryDrawerItem().withName(R.string.setting_name).withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(Identifier.SETTING.value.toLong()).withSelectable(false)
                )
                .withOnDrawerItemClickListener({
                    _,_,drawerItem->
                    if (drawerItem.identifier > Identifier.COLLECT.value && drawerItem is PrimaryDrawerItem) {
                        mBinding.layoutToolbar.toolbar.title = getString(drawerItem.identifier.toInt())
                        switchPage(drawerItem.identifier.toInt())
                    }
                    false
                })
                .build()
        initDrawerHeader(drawer)
        drawer.setSelection(nowIdentifier.toLong())

        Log.d("Main",userPref.accessToken.toString())
        Log.d("Main",userPref.expires.toString())
    }

    fun initDrawerHeader(drawer : Drawer){
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

    fun switchPage(identifier : Int){
        if(identifier != nowIdentifier || isFirst){
            nowIdentifier = identifier
            when(identifier){
                Identifier.HOME_ILLUSTRATIONS.value -> {
                    replaceFragment(IllustFragment(account.obsToken(this).flatMap({token ->
                        apiService.getRecommendedIllusts(token, true) }),account,false))
                }
                Identifier.HOME_MANGA.value -> {
                    replaceFragment(IllustFragment(account.obsToken(this).flatMap({token ->
                        apiService.getRecommendedMangaList(token, true) }),account,true))
                }
            }
        }
    }

    fun replaceFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Identifier",nowIdentifier)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    enum class Identifier(val value : Int){
        HOME_ILLUSTRATIONS(R.string.home_illust),
        HOME_MANGA(R.string.home_mange),
        HOME_NOVEL(R.string.home_novel),
        NEWEST_FOLLOW(R.string.newest_follow),
        NEWEST_NEW(R.string.newest_new),
        NEWEST_MY_PIXIV(R.string.newest_my_pixiv),
        PIXIVISION(R.string.pixivision_name),
        COLLECT(401),
        BROWSING_HISTORY(301),
        USER(201),
        SETTING(101);
    }
}