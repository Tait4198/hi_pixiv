package info.hzvtc.hipixiv.vm

import android.support.v4.content.ContextCompat
import com.hippo.glgallery.GalleryView
import com.hippo.glgallery.SimpleAdapter
import info.hzvtc.hipixiv.R
import info.hzvtc.hipixiv.databinding.ActivityImageBinding
import info.hzvtc.hipixiv.util.ImageGalleryProvider
import info.hzvtc.hipixiv.view.ImageActivity
import javax.inject.Inject

class ImageViewModel @Inject constructor(): BaseViewModel<ImageActivity, ActivityImageBinding>(), GalleryView.Listener {

    private var urls = ArrayList<String>()
    private lateinit var galleryProvider : ImageGalleryProvider

    override fun initViewModel() {
        urls = mView.intent.getStringArrayListExtra(getString(R.string.extra_list))
        val nowPosition = mView.intent.getIntExtra(getString(R.string.extra_int),2)
        val edgeColorId = if (urls.size > 1) R.color.colorEdge else R.color.colorEdgeTra
        galleryProvider = ImageGalleryProvider(urls,mView)
        val imageAdapter = SimpleAdapter(mBind.glRootView,galleryProvider)
        imageAdapter.setShowIndex(urls.size > 1)
        val galleryView = GalleryView.Builder(mView,imageAdapter)
                .setListener(this)
                .setStartPage(nowPosition)
                .setProgressColor(getColor(R.color.primary))
                .setProgressSize(120)
                .setEdgeColor(getColor(edgeColorId))
                .setErrorTextSize(64)
                .setErrorTextColor(getColor(R.color.md_red_500))
                .setBackgroundColor(getColor(R.color.md_grey_850))
                .build()
        mBind.glRootView.setContentPane(galleryView)
        galleryProvider.setListener(imageAdapter)
        galleryProvider.setGLRoot(mBind.glRootView)
        galleryProvider.start()
    }

    override fun onLongPressPage(index: Int) {

    }

    override fun onTapSliderArea() {

    }

    override fun onTapMenuArea() {

    }

    override fun onUpdateCurrentIndex(index: Int) {

    }

    private fun getColor(resId : Int) : Int = ContextCompat.getColor(mView,resId)

    fun stop(){
        galleryProvider.stop()
    }
}