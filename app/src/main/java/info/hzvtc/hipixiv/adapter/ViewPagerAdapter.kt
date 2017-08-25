package info.hzvtc.hipixiv.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import info.hzvtc.hipixiv.view.fragment.BaseFragment

class ViewPagerAdapter<T : BaseFragment<*>>(private val pagers : Array<T>, private val titles : Array<String>,
                                            fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = pagers[position]

    override fun getCount(): Int = pagers.size

    override fun getPageTitle(position: Int) = if (pagers.size == titles.size) titles[position] else ""

}