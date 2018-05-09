package info.hzvtc.hipixiv.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.RankingType
import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.databinding.ActivityRankingBinding
import info.hzvtc.hipixiv.databinding.DialogRankingFilterBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.vm.RankingViewModel
import java.util.*
import javax.inject.Inject

class RankingActivity : BindingActivity<ActivityRankingBinding>(),RootActivity {

    @Inject
    lateinit var viewModel : RankingViewModel
    @Inject
    lateinit var account : Account
    @Inject
    lateinit var apiService : ApiService
    @Inject
    lateinit var userPref : UserPreferences

    private lateinit var restrictArray : Array<Int>
    private var modePosition = 0
    private lateinit var modeItemArray : Array<String>
    private lateinit var modeParamArray : Array<String>
    private lateinit var modeParam : String
    private lateinit var date : String
    private lateinit var today : String
    private var year = 0
    private var month = 0
    private var day = 0

    override fun getLayoutId(): Int = R.layout.activity_ranking

    override fun initView(savedInstanceState: Bundle?) {
        component.inject(this)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE,-1)
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        today = "$year-$month-$day"
        date = today
        val typeValue : Int = intent.getIntExtra(getString(R.string.extra_string),0)
        when(typeValue){
            RankingType.ILLUST.value ->{
                mBinding.layoutToolbar?.toolbar?.title ?: getString(R.string.ranking_illust_title)
                modeItemArray = resources.getStringArray(R.array.ranking_illust_items)
                restrictArray = arrayOf(7,11,12)
                modeParamArray = resources.getStringArray(R.array.ranking_illust_parameters)
                modeParam = modeParamArray[modePosition]
                viewModel.obsIllustNewData = account.obsToken(this).flatMap({token -> apiService.getIllustRanking(token,modeParam)})
            }
            RankingType.MANGA.value ->{
                mBinding.layoutToolbar?.toolbar?.title ?: getString(R.string.ranking_manga_title)
                modeItemArray = resources.getStringArray(R.array.ranking_manga_items)
                restrictArray = arrayOf(4,6,7)
                modeParamArray = resources.getStringArray(R.array.ranking_manga_parameters)
                modeParam = modeParamArray[modePosition]
                viewModel.obsIllustNewData = account.obsToken(this).flatMap({token -> apiService.getIllustRanking(token,modeParam)})
            }
        }
        setSupportActionBar(mBinding.layoutToolbar?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.layoutToolbar?.toolbar?.setNavigationOnClickListener({onBackPressed()})
        mBinding.fab.setOnClickListener({
            showFilterDialog()
        })
        viewModel.apiService = apiService
        viewModel.account = account
        viewModel.setView(this)
    }

    private fun showFilterDialog() {
        val dialog = MaterialDialog.Builder(this)
                .title(getString(R.string.select_ranking))
                .customView(R.layout.dialog_ranking_filter, true)
                .positiveText(getString(R.string.app_dialog_ok))
                .negativeText(R.string.app_dialog_cancel)
                .onPositive({_,_ -> choose()})
                .build()
        val bind = DataBindingUtil.bind<DialogRankingFilterBinding>(dialog.customView!!)
        val itemArray = Array(restrictArray[userPref.xRestrict?:0],{""})
        copy(modeItemArray,itemArray)
        bind?.mode?.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemArray)
        bind?.mode?.setSelection(modePosition)
        bind?.mode?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                modePosition = position
                modeParam = modeParamArray[position]
            }
        }
        bind?.datePicker?.init(year,month,day) { _, y, m, d ->
            year = y
            month = m
            day = d
            date = "$y-${m + 1}-$d"
        }
        dialog.show()
    }

    override fun showFab(status : Boolean){
        if(status) mBinding.fab.show() else mBinding.fab.hide()
    }

    fun choose(){
        if(viewModel.isIllust){
            val obs = account.obsToken(this)
                    .flatMap({
                        token ->
                        if(today == date)
                            return@flatMap apiService.getIllustRanking(token,modeParam)
                        else
                            return@flatMap apiService.getIllustRanking(token,modeParam,date)
                    })
            viewModel.getIllustData(obs)
        }
    }

    fun copy(from: Array<String>, to: Array<String>) {
        assert(from.size > to.size)
        for (i in to.indices)
            to[i] = from[i]
    }

    override fun getRootView(): CoordinatorLayout {
        return mBinding.rootView
    }
}