package info.hzvtc.hipixiv.adapter.events

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

abstract class OnScrollListener : RecyclerView.OnScrollListener() {

    private var layoutManagerType: LAYOUT_MANAGER_TYPE? = null
    private var lastPositions: IntArray? = null
    private var lastVisibleItemPosition: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager
        if (layoutManagerType == null) {
            if (layoutManager is LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR
            } else if (layoutManager is StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID
            } else {
                throw RuntimeException("Unsupported LayoutManager used. Valid ones are " +
                        "LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager")
            }
        }

        when (layoutManagerType) {
            LAYOUT_MANAGER_TYPE.LINEAR -> lastVisibleItemPosition = (layoutManager as LinearLayoutManager)
                    .findLastVisibleItemPosition()
            LAYOUT_MANAGER_TYPE.GRID -> lastVisibleItemPosition = (layoutManager as GridLayoutManager)
                    .findLastVisibleItemPosition()
            LAYOUT_MANAGER_TYPE.STAGGERED_GRID -> {
                val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager
                if (lastPositions == null) {
                    lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions)
                lastVisibleItemPosition = findMax(lastPositions as IntArray)
            }
        }

        if (dy < 0) {
            scrollDown(dy)
        } else if (dy > 0) {
            scrollUp(dy)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val currentScrollState = newState
        val layoutManager = recyclerView.layoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        if (visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition >= totalItemCount - 1) {
            onBottom()
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        return lastPositions.max()?: lastPositions[0]
    }

    abstract fun onBottom()

    open fun scrollUp(dy : Int){
        //
    }

    open fun scrollDown(dy : Int){
        //
    }

    enum class LAYOUT_MANAGER_TYPE{
        LINEAR,
        GRID,
        STAGGERED_GRID
    }
}