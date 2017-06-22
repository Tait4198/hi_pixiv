package info.hzvtc.hipixiv.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import info.hzvtc.hipixiv.view.fragment.BaseFragment

class ViewPagerAdapter<T : BaseFragment>(val pagers : Array<T>,val titles : Array<String>,
                                         fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = pagers[position]

    override fun getCount(): Int = pagers.size

    override fun getPageTitle(position: Int) = if (pagers.size == titles.size) titles[position] else ""

}