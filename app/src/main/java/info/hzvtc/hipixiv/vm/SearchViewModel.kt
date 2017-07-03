package info.hzvtc.hipixiv.vm

import android.databinding.DataBindingUtil
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.gson.Gson
import info.hzvtc.hipixiv.R

import info.hzvtc.hipixiv.data.Account
import info.hzvtc.hipixiv.data.UserPreferences
import info.hzvtc.hipixiv.data.ViewPagerBundle
import info.hzvtc.hipixiv.databinding.ActivitySearchBinding
import info.hzvtc.hipixiv.databinding.DialogSearchFilterBinding
import info.hzvtc.hipixiv.net.ApiService
import info.hzvtc.hipixiv.pojo.AutoCompleteResponse
import info.hzvtc.hipixiv.pojo.Suggestion
import info.hzvtc.hipixiv.util.AppUtil
import info.hzvtc.hipixiv.view.SearchActivity
import info.hzvtc.hipixiv.view.fragment.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


//todo 其他Activity Tag
class SearchViewModel @Inject constructor(val userPref: UserPreferences,val account : Account,val apiService: ApiService)
    : BaseViewModel<SearchActivity, ActivitySearchBinding>(){

    private val gson = Gson()

    private lateinit var obsToken : Observable<String>
    private lateinit var vpFragment : ViewPagerFragment
    private lateinit var mainBundle : ViewPagerBundle<BaseFragment<*>>
    private lateinit var searchBundle : ViewPagerBundle<BaseFragment<*>>

    private lateinit var sortArray : Array<String>
    private lateinit var targetArray : Array<String>
    private lateinit var durationArray : Array<String>
    private var sortPosition = 0
    private var targetPosition = 0
    private var durationPosition = 0
    private var query = ""
    private var sort = ""
    private var target = ""
    private var duration: String? = null

    override fun initViewModel() {
        obsToken = account.obsToken(mView)
        sortArray = mView.resources.getStringArray(R.array.search_sort_parameters)
        targetArray = mView.resources.getStringArray(R.array.search_target_parameters)
        durationArray = mView.resources.getStringArray(R.array.search_duration_parameters)
        sort = sortArray[sortPosition]
        target = targetArray[targetPosition]

        mBind.searchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if(oldQuery != "" && newQuery == ""){
                setHistoryComplete()
                vpFragment.updateBundle(mainBundle)
                mView.setFabVisible(false,false)
            }else{
                setAutoComplete(newQuery)
            }
        }
        mBind.searchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener{
            override fun onFocusCleared() {
                mBind.searchView.clearSuggestions()
            }
            override fun onFocus() {
                setHistoryComplete()
            }
        })
        mBind.searchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener{
            override fun onSearchAction(currentQuery: String?) {
                doQuery(currentQuery?:"")
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                mBind.searchView.setSearchText(searchSuggestion?.body)
                onSearchAction(searchSuggestion?.body)
            }
        })
        mBind.searchView.setOnBindSuggestionCallback { _, leftIcon, textView, item, _ ->
            if(item is Suggestion){
                val resId = if (item.isHistory) R.drawable.ic_history_grey else R.drawable.ic_search_grey
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(mView.resources, resId, null))
                textView.text = item.suggestion
            }
        }


        mainBundle = object : ViewPagerBundle<BaseFragment<*>>() {
            init {
                titles = mView.resources.getStringArray(R.array.search_tab)
                pagers = arrayOf(
                        TrendTagLazyFragment(obsToken.flatMap({ token -> apiService.getIllustTrendTags(token)})),
                        UserLazyFragment(obsToken.flatMap({ token -> apiService.getUserRecommended(token)}),account))
            }
        }

        searchBundle = object : ViewPagerBundle<BaseFragment<*>>() {
            init {
                titles = mView.resources.getStringArray(R.array.search_tab)
                pagers = arrayOf(
                        IllustSearchFragment(account),UserSearchFragment(account)
                )
            }

            override fun fabClick() {
                super.fabClick()
                if(nowPosition == 0) showSearchFilter()
            }

            override fun fabShow(position: Int) {
                super.fabShow(position)
                if(position == 0)
                    mView.setFabVisible(true,true)
                else
                    mView.setFabVisible(false,false)
            }
        }
        vpFragment = ViewPagerFragment(mainBundle)

        replaceFragment(vpFragment)
    }

    fun searchByTrendTag(tag : String){
        mBind.searchView.setSearchText(tag)
        doQuery(tag)
    }

    private fun setHistoryComplete(){
        Observable.just(userPref.historyJson?:"")
                .filter{value -> value != ""}
                .map{ value -> gson.fromJson(value,AutoCompleteResponse::class.java)}
                .map{ (keywords) ->
                    val suggestions : MutableList<Suggestion> = ArrayList()
                    keywords.forEach { word -> suggestions.add(Suggestion(word,true)) }
                    return@map suggestions }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    suggestions -> mBind.searchView.swapSuggestions(suggestions)
                })
    }

    private fun setAutoComplete(value : String){
        account.obsToken(mView)
                .filter { AppUtil.isNetworkConnected(mView) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{ mBind.searchView.showProgress()}
                .observeOn(Schedulers.io())
                .flatMap{ token -> apiService.getSearchAutoCompleteKeywords(token,value)}
                .map{ (keywords) ->
                    val suggestions : MutableList<Suggestion> = ArrayList()
                    keywords.forEach { word -> suggestions.add(Suggestion(word,false)) }
                    return@map suggestions }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    suggestions -> mBind.searchView.swapSuggestions(suggestions)
                },{
                    error ->
                    mBind.searchView.hideProgress()
                    Log.e("Error",error.printStackTrace().toString())
                },{
                    mBind.searchView.hideProgress()
                })
    }

    private fun replaceFragment(fragment : Fragment){
        val transaction = mView.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame,fragment)
        transaction.commit()
    }

    private fun doQuery(currentQuery : String){
        query = currentQuery
        if(query != ""){
            pushNewWord(query)
            mView.setFabVisible(true,true)
            vpFragment.updateBundle(searchBundle)
            (searchBundle.pagers[0] as IllustSearchFragment)
                    .setTempObs(obsToken.flatMap{token -> apiService.getSearchIllust(token,query,sort,target,duration)})
            (searchBundle.pagers[1] as UserSearchFragment)
                    .setTempObs(obsToken.flatMap { token -> apiService.getSearchUser(token,query) })
            mBind.fab.setOnClickListener { showSearchFilter() }
        }
    }

    private fun showSearchFilter(){
        val dialog = MaterialDialog.Builder(mView)
                .title(mView.getString(R.string.search_filter_title))
                .customView(R.layout.dialog_search_filter, true)
                .positiveText(mView.getString(R.string.search_filter))
                .negativeText(R.string.app_dialog_cancel)
                .onPositive({ _, _ -> doQuery(query) })
                .build()

        val bind = DataBindingUtil.bind<DialogSearchFilterBinding>(dialog?.customView)
        bind.sort.adapter = ArrayAdapter<String>(mView,android.R.layout.simple_list_item_1,
                if(userPref.isPremium?:false) mView.resources.getStringArray(R.array.sort_premium_items)
                else mView.resources.getStringArray(R.array.sort_items))
        bind.searchTarget.adapter = ArrayAdapter<String>(mView,android.R.layout.simple_list_item_1,
                mView.resources.getStringArray(R.array.search_target_items))
        bind.duration.adapter = ArrayAdapter<String>(mView,android.R.layout.simple_list_item_1,
                mView.resources.getStringArray(R.array.duration_items))
        bind.sort.setSelection(sortPosition)
        bind.duration.setSelection(durationPosition)
        bind.searchTarget.setSelection(targetPosition)
        bind.sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortPosition = position
                sort = sortArray[position]
            }
        }
        bind.searchTarget.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                targetPosition = position
                target = targetArray[position]
            }
        }
        bind.duration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    duration = null
                }else{
                    durationPosition = position
                    duration = durationArray[position]
                }
            }
        }

        dialog.show()
    }

    private fun pushNewWord(value : String){
        val suggestions = if(userPref.historyJson.isNullOrEmpty())
            AutoCompleteResponse()
        else
            gson.fromJson(userPref.historyJson,AutoCompleteResponse::class.java)
        if(!suggestions.keywords.contains(value)){
            suggestions.keywords.add(0,value)
            if(suggestions.keywords.size >= 8) suggestions.keywords.removeAt(7)
        }
        userPref.historyJson = gson.toJson(suggestions)
    }
}