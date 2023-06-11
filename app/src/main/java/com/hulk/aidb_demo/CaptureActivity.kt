package com.hulk.aidb_demo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.hulk.aidb.AIDBMeta
import com.hulk.aidb_demo.utils.AIDBMetaBinder
import com.hulk.aidb_demo.utils.BitmapBinder
import com.hulk.aidb_demo.utils.ScanFrameView
import com.hulk.aidb_demo.utils.VisualAiDB
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


class CaptureActivity : AppCompatActivity() {

    lateinit var visualBitmap: Bitmap
    lateinit var mScanFrameView: ScanFrameView
    var costTime: Long = 0
    external fun aidbForward(bitmap: Bitmap): AIDBMeta

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when(msg.what){
                0 ->{
                    mScanFrameView.visibility = View.INVISIBLE
                    val imageViewCapture: ImageView = findViewById(R.id.imageViewCapture)
                    imageViewCapture.setImageBitmap(visualBitmap)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        // hide ActionBar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // 同时隐藏状态栏和导航栏
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        mScanFrameView = findViewById(R.id.sfvBackground)

        val bundle = intent.extras
        val bitmapBinder = bundle!!.getBinder("image") as BitmapBinder?
        val bitmap: Bitmap = bitmapBinder!!.bitmap

        val imageViewCapture: ImageView = findViewById(R.id.imageViewCapture)
//        imageViewCapture.setImageBitmap(VisualAiDB.mirrorBitmap(bitmap))
        imageViewCapture.setImageBitmap(bitmap)

        mScanFrameView.visibility = View.VISIBLE
        mScanFrameView.startScan()

//        val bundleMeta = intent.extras
//        val aidbMetaBinder = bundleMeta!!.getBinder("meta") as AIDBMetaBinder?
//        val aidbMeta: AIDBMeta = aidbMetaBinder!!.aidbMeta
        thread {
            var aidbMeta: AIDBMeta
            costTime = measureTimeMillis {
                aidbMeta = aidbForward(bitmap)
            }
            val mirror = intent.getBooleanExtra("mirror", false)

            visualBitmap = VisualAiDB.visual(
                this,
                bitmap,
                aidbMeta,
                mirror
            )

            val msg = Message()
            msg.what = 0
            handler.sendMessage(msg)
        }

    }
}