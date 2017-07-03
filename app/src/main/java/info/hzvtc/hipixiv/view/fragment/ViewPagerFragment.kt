package info.hzvtc.hipixiv.view.fragment

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.adapter.ViewPagerAdapter
import info.hzvtc.hipixiv.data.ViewPagerBundle
import info.hzvtc.hipixiv.databinding.FragmentViewPagerBinding

class ViewPagerFragment(val bundle : ViewPagerBundle<BaseFragment<*>>) : BindingFragment<FragmentViewPagerBinding>(){

    private lateinit var pagerAdapter : ViewPagerAdapter<BaseFragment<*>>

    override fun getLayoutId(): Int = R.layout.fragment_view_pager

    override fun initView(binding: FragmentViewPagerBinding): View {
        pagerAdapter = ViewPagerAdapter(bundle.pagers,bundle.titles,childFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = pagerAdapter.count
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                //
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //
            }

            override fun onPageSelected(position: Int) {
                bundle.fabShow(position)
            }
        })
        return binding.root
    }

    fun updateBundle(bundle : ViewPagerBundle<BaseFragment<*>>){
        pagerAdapter = ViewPagerAdapter(bundle.pagers,bundle.titles,childFragmentManager)
        mBinding.viewPager.adapter = pagerAdapter
        mBinding.viewPager.offscreenPageLimit = pagerAdapter.count
        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                //
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //
            }

            override fun onPageSelected(position: Int) {
                bundle.fabShow(position)
            }
        })
    }

}
