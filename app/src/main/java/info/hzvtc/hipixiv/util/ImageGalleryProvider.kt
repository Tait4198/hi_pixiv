package info.hzvtc.hipixiv.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Process
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.hippo.glgallery.GalleryPageView
import com.hippo.glgallery.GalleryProvider
import com.hippo.image.Image
import com.hippo.yorozuya.thread.PriorityThread
import info.hzvtc.hipixiv.R
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ImageGalleryProvider(val urls : List<String>,val context: Context) : GalleryProvider(),Runnable{

    private lateinit var mThread: Thread
    private val mRequests = Stack<Int>()
    private val mDecodingIndex = AtomicInteger(GalleryPageView.INVALID_INDEX)

    override fun run() {
        while (!Thread.currentThread().isInterrupted){
            if (mRequests.isEmpty()) {
                continue
            }
            val index = mRequests.pop()
            mDecodingIndex.lazySet(index)
            val imagePipeline = Fresco.getImagePipeline()
            val source = imagePipeline.fetchDecodedImage(ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(urls[index])).build(), this)
            source.subscribe(object : BaseBitmapDataSubscriber() {
                override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
                    notifyPageFailed(index,context.getString(R.string.not_load))
                }

                override fun onNewResultImpl(bitmap: Bitmap?) {
                    notifyPageSucceed(index, Image.create(bitmap))
                }

            }, CallerThreadExecutor.getInstance())
        }
    }

    override fun start() {
        super.start()
        mThread = PriorityThread(this, ImageGalleryProvider::class.java.name, Process.THREAD_PRIORITY_BACKGROUND)
        mThread.start()
    }

    override fun stop() {
        super.stop()
        mThread.interrupt()
    }

    override fun getError(): String {
        return "Gallery Error"
    }

    override fun onCancelRequest(index: Int) {
        mRequests.remove(index)
    }

    override fun onForceRequest(index: Int) {
        onRequest(index)
    }

    override fun size(): Int {
        return urls.size
    }

    override fun onRequest(index: Int) {
        if (!mRequests.contains(index) && index != mDecodingIndex.get()) {
            mRequests.add(index)
        }
        notifyPageWait(index)
    }
}