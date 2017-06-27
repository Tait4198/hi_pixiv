package info.hzvtc.hipixiv.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

class SimplePagerAdapter(val views : Array<View>,val titles : Array<String>) : PagerAdapter() {

    override fun isViewFromObject(view: View?, obj: Any?) = view == obj

    override fun getCount() = views.size

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        container?.addView(views[position])
        return views[position]
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(views[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}