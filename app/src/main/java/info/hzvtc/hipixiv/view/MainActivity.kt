package info.hzvtc.hipixiv.view

import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityMainBinding
import info.hzvtc.hipixiv.vm.MainViewModel
import javax.inject.Inject
import android.content.Intent
import android.support.v4.content.ContextCompat
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
import info.hzvtc.hipixiv.util.AppMessage


class MainActivity : BindingActivity<ActivityMainBinding>() {

    @Inject
    lateinit var viewModel : MainViewModel
    @Inject
    lateinit var account : Account
    @Inject
    lateinit var userPref : UserPreferences

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        component.inject(this)
        viewModel.setView(this)
        mBinding.layoutToolbar.toolbar.setTitle(R.string.app_name)
        setSupportActionBar(mBinding.layoutToolbar.toolbar)

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
                                        withIdentifier(Identifier.HOME_ILLUSTRATIONS.value).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.home_mange).
                                        withIdentifier(Identifier.HOME_MANGA.value).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.home_novel).
                                        withIdentifier(Identifier.HOME_NOVEL.value).withLevel(2)
                        ),
                        ExpandableDrawerItem().withName(R.string.newest_name).withIcon(GoogleMaterial.Icon.gmd_fiber_new)
                                .withSelectable(false).withSubItems(
                                PrimaryDrawerItem().withName(R.string.newest_follow).
                                        withIdentifier(Identifier.NEWEST_FOLLOW.value).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.newest_name).
                                        withIdentifier(Identifier.NEWEST_NEW.value).withLevel(2),
                                PrimaryDrawerItem().withName(R.string.newest_my_pixiv).
                                        withIdentifier(Identifier.NEWEST_MY_PIXIV.value).withLevel(2)
                        ),
                        PrimaryDrawerItem().withName(R.string.pixivision_name).withIcon(GoogleMaterial.Icon.gmd_highlight)
                                .withIdentifier(Identifier.PIXIVISION.value),
                        dividerItem,
                        PrimaryDrawerItem().withName(R.string.collect_name).withIcon(GoogleMaterial.Icon.gmd_favorite)
                                .withIdentifier(Identifier.COLLECT.value),
                        PrimaryDrawerItem().withName(R.string.browsing_name).withIcon(GoogleMaterial.Icon.gmd_history)
                                .withIdentifier(Identifier.BROWSING_HISTORY.value),
                        PrimaryDrawerItem().withName(R.string.user_name).withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(Identifier.USER.value),
                        dividerItem,
                        PrimaryDrawerItem().withName(R.string.setting_name).withIcon(GoogleMaterial.Icon.gmd_settings)
                                .withIdentifier(Identifier.SETTING.value)
                )
                .build()
        drawer.setSelection(Identifier.HOME_ILLUSTRATIONS.value)
        initDrawer(drawer)

        account.obsToken(this).subscribe({
            t ->
            AppMessage.logInfo(t)
            AppMessage.logInfo(userPref.expires.toString())
        })
    }

    fun initDrawer(drawer : Drawer){
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

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

    enum class Identifier(val value : Long){
        HOME_ILLUSTRATIONS(1001), HOME_MANGA(1002), HOME_NOVEL(1003),
        NEWEST_FOLLOW(2001), NEWEST_NEW(2002), NEWEST_MY_PIXIV(2003),
        PIXIVISION(3001), COLLECT(4001),
        BROWSING_HISTORY(5001), USER(6001), SETTING(7001);
    }
}