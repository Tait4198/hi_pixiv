package info.hzvtc.hipixiv.view

import android.support.design.widget.CoordinatorLayout

interface RootActivity {
    fun getRootView() : CoordinatorLayout
    fun showFab(status : Boolean)
}