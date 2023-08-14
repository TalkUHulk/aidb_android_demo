package com.hulk.aidb_demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat.getInsetsController
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hulk.aidb.AIDBMeta
import com.hulk.aidb_demo.utils.AIDBMetaBinder
import com.hulk.aidb_demo.utils.AnalysisAIDB
import com.hulk.aidb_demo.utils.BitmapBinder
import com.hulk.aidb_demo.utils.BoundsOverlay
import com.hulk.aidb_demo.utils.CameraViewModel
import com.hulk.aidb_demo.utils.VisualAiDB
import com.hulk.aidb_demo.utils.rotateBitmap
import java.io.*
import java.lang.String.format
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis


class ProcessActivity : AppCompatActivity(){

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var executor: ExecutorService
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imageCapture: ImageCapture

    private lateinit var viewfinder: PreviewView
    private lateinit var boundsOverlay: BoundsOverlay

    private lateinit var camera: Camera

    private var LENS_FACING_TYPE = CameraSelector.LENS_FACING_BACK
    private var mMode = 0

    private lateinit var mFPSTextView: TextView
    private lateinit var mModelTextView: TextView
    private lateinit var mBackendTextView: TextView
    private lateinit var mModeTextView: TextView
    companion object {
        private const val TAG = "Hulk-AIDB"
        // 加载生成的动态链接库
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("AiDB")
            System.loadLibrary("MNN");
            System.loadLibrary("openvino");


            try {
                System.loadLibrary("MNN_CL")
                System.loadLibrary("MNN_Express")
                System.loadLibrary("MNN_Vulkan")
            } catch (ce: Throwable) {
                Log.w("MNNJNI", "load MNN GPU so exception=%s", ce)
            }
            System.loadLibrary("mnncore")
        }

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        lateinit  var appContext: Context
    }


    private val handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            mFPSTextView.text = "${1000 / msg.what}fps"

        }
    }

    /**
     * The display listener is used to update the ImageAnalysis's target rotation for cases where the
     * Activity isn't recreated after a device rotation, for example a 180 degree rotation from
     * landscape to reverse-landscape on a portrait oriented device.
     */
    private val displayListener: DisplayManager.DisplayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {}
        override fun onDisplayRemoved(displayId: Int) {}
        override fun onDisplayChanged(displayId: Int) {
            val rootView = findViewById<View>(android.R.id.content)
            if (::imageAnalysis.isInitialized && displayId == rootView.display.displayId) {
                imageAnalysis.targetRotation = rootView.display.rotation
            }
        }
    }

    // 声明JNI函数，对应native-lib.cpp里定义的函数
    external fun aidbCreateInstance(model_name: String, backend: String, config: String)
    external fun aidbReleaseInstance()
    external fun aidbForward(bitmap: Bitmap): AIDBMeta

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_process)

        // hide ActionBar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // 同时隐藏状态栏和导航栏
        val controller = getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        appContext = applicationContext

        val model = intent.getIntExtra("model", 8)
        val backend = intent.getIntExtra("backend", 1)
        mMode = intent.getIntExtra("mode", 0) // 0:catpure, 1:analyse

        viewfinder = findViewById(R.id.viewfinder)
        boundsOverlay = findViewById(R.id.boundsOverlay)

        val btn_switch: Button = findViewById(R.id.buttonSwitch)
        btn_switch.setOnClickListener{
            switchCamera()
        }

        val capture_switch: Button = findViewById(R.id.captureSwitch)
        mFPSTextView = findViewById(R.id.tvFPS)
        mBackendTextView = findViewById(R.id.tvBackend)
        mModelTextView = findViewById(R.id.tvModel)
        mModeTextView = findViewById(R.id.tvMode)

        if(0 == mMode){
            capture_switch.setOnClickListener{
                takeCapture()
            }
            mFPSTextView.isInvisible = true
            mModeTextView.setText("Capture")
        } else {
            capture_switch.isInvisible = true
            mFPSTextView.isInvisible = false
            mModeTextView.setText("Analyse")
        }

        var modelStr = "scrfd_500m_kps"
        when (model) {
            0 -> modelStr = "scrfd_500m_kps"
            1 -> modelStr = "pfpld"
            2 -> modelStr = "3ddfa_mb05_bfm_base"
            3 -> modelStr = "3ddfa_mb05_bfm_dense"
            4 -> modelStr = "bisenet"
            5 -> modelStr = "movenet"
            6 -> modelStr = "yolox_nano"
            7 -> modelStr = "yolov7_tiny"
            8 -> modelStr = "yolov8n"
            9 -> modelStr = "mobilevit_xxs"
            10 -> modelStr = "ppocr"
            else -> {
                Log.d(TAG, "model type $model is not support, auto set SCRFD.")
            }
        }
        mModelTextView.text = modelStr.split("_")[0]
//                ONNX = 1,
//                MNN = 2,
//                NCNN = 3,
//                TNN = 4,
//                OPENVINO = 5,
//                PADDLE_LITE = 6,
        var backendStr = "mnn"
        when (backend) {
            1 -> backendStr = "onnx"
            2 -> backendStr = "mnn"
            3 -> backendStr = "ncnn"
            4 -> backendStr = "tnn"
            5 -> backendStr = "openvino"
            6 -> backendStr = "paddlelite"
            else -> {
                Log.d(TAG, "backend $backend is not support, auto set MNN.")
            }
        }

        mBackendTextView.text = backendStr

        val modelsZoo = File(obbDir, "config")
        aidbCreateInstance(modelStr, backendStr, modelsZoo.absolutePath)


        if(1 == mMode){
            // Initialize analysis executor
            executor = Executors.newSingleThreadExecutor()
        }


        // Set up camera
        setUpCameraViewModel()

        // Request permissions if not already granted
        if (!arePermissionsGranted()) {
            requestPermissions()
        }

        // Register the display listener
        val manager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        manager.registerDisplayListener(displayListener, null)
    }


    private fun setUpCameraViewModel() {
        val viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        viewModel
            .processCameraProvider
            .observe(
                this,
                Observer { provider: ProcessCameraProvider? ->
                    cameraProvider = provider ?: return@Observer
                    tryStartCamera()
                }
            )
    }

    private fun switchCamera() {
        // 解除绑定
        cameraProvider.unbindAll()
        LENS_FACING_TYPE = if(LENS_FACING_TYPE == CameraSelector.LENS_FACING_FRONT)
            CameraSelector.LENS_FACING_BACK
        else
            CameraSelector.LENS_FACING_FRONT

        startCamera(cameraProvider)
    }

    private fun takeCapture() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this), // Defines where the callbacks are run
            object : ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val image = imageProxy.image
                    if (image == null) {
                        imageProxy.close()
                        return
                    }
                    val rotation = imageProxy.imageInfo.rotationDegrees

                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    val option = BitmapFactory.Options()
                    option.outConfig = Bitmap.Config.ARGB_8888
                    val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, option)
                    val rotateBitmap = rotateBitmap(bitmapImage, rotation.toFloat())
//                    var aidbMeta: AIDBMeta
//                    val time = measureTimeMillis {
//                        aidbMeta = aidbForward(rotateBitmap)
//                    }

//                    val visualBitmap = VisualAiDB.visual(
//                        appContext,
//                        rotateBitmap,
//                        aidbMeta,
//                        LENS_FACING_TYPE == CameraSelector.LENS_FACING_FRONT)
//                    Log.d(TAG, "====>>>onCaptureSuccess Cost: $time")
                    imageProxy.close() // Make sure to close the image

                    val intent = Intent("com.hulk.aidb_demo.PROCESS_SHOW")

                    val bundle = Bundle()
                    bundle.putBinder("image", BitmapBinder(rotateBitmap))
                    intent.putExtras(bundle)

//                    val bundleMeta = Bundle()
//                    bundleMeta.putBinder("meta", AIDBMetaBinder(aidbMeta))
//                    intent.putExtras(bundleMeta)

                    intent.putExtra("mirror", LENS_FACING_TYPE == CameraSelector.LENS_FACING_FRONT)
                    startActivity(intent)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG, "====>>>onCaptureSuccess onError")
                    // Handle exception
                }
            }
        )
    }
    private fun tryStartCamera() {
        if (::cameraProvider.isInitialized && arePermissionsGranted()) {
            startCamera(cameraProvider)
        }
    }

    /**
     * Sets up both preview and image analysis use cases, both with the same target aspect ratio
     * (4:3). The viewfinder also has an aspect ratio of 4:3. This makes it so that the frames the
     * ImageAnalysis use case receives from the camera match (in aspect ratio) the frames the
     * viewfinder displays (through the Preview use case), and guarantees the face bounds drawn on
     * top of the viewfinder have the correct coordinates.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera(cameraProvider: ProcessCameraProvider) {
        // Build Preview use case, and set its SurfaceProvider
        viewfinder.setOnTouchListener { view, event ->
            tapToFocus(event)
        }
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        preview.setSurfaceProvider(viewfinder.surfaceProvider)

        val lensFacing = getCameraLensFacing(cameraProvider)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        if(0 == mMode){
            imageCapture = ImageCapture.Builder().build()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } else{
            // Build an ImageAnalysis use case, and hook it up with a AIDB
            imageAnalysis = ImageAnalysis.Builder()
//            .setTargetResolution(Size(1280, 720))
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            setAIDB(imageAnalysis, lensFacing)

            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }


    }

    private fun tapToFocus(event: MotionEvent): Boolean {

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                val point = viewfinder.meteringPointFactory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point).build()
                camera.cameraControl.startFocusAndMetering(action)
                true
            }
            else -> false
        }
    }


    private fun getCameraLensFacing(cameraProvider: ProcessCameraProvider): Int {
        try {
            if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) && LENS_FACING_TYPE == CameraSelector.LENS_FACING_BACK) {
                return CameraSelector.LENS_FACING_BACK
            }
            if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) && LENS_FACING_TYPE == CameraSelector.LENS_FACING_FRONT) {
                return CameraSelector.LENS_FACING_FRONT
            }
            Toast.makeText(this, R.string.no_available_camera, Toast.LENGTH_LONG).show()
            finish()
        } catch (exception: CameraInfoUnavailableException) {
            Toast.makeText(this, R.string.camera_selection_error, Toast.LENGTH_LONG).show()
            Log.e(TAG, "An error occurred while choosing a CameraSelector ", exception)
            finish()
        }
        throw IllegalStateException("An error occurred while choosing a CameraSelector ")
    }

    /**
     * The face detector provides face bounds whose coordinates, width and height depend on the
     * preview's width and height, which is guaranteed to be available after the preview starts
     * streaming.
     */
    private fun setAIDB(imageAnalysis: ImageAnalysis, lensFacing: Int) {
        viewfinder.previewStreamState.observe(this, object : Observer<PreviewView.StreamState> {
            override fun onChanged(streamState: PreviewView.StreamState?) {
                if (streamState != PreviewView.StreamState.STREAMING) {
                    return
                }

                val preview = viewfinder.getChildAt(0)
                var width = preview.width * preview.scaleX
                var height = preview.height * preview.scaleY
                val rotation = preview.display.rotation
                if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                    val temp = width
                    width = height
                    height = temp
                }

                imageAnalysis.setAnalyzer(
                    executor,
                    createAIDB(width.toInt(), height.toInt(), lensFacing)
                )
                viewfinder.previewStreamState.removeObserver(this)
            }
        })


    }

    
    private fun createAIDB(
        viewfinderWidth: Int,
        viewfinderHeight: Int,
        lensFacing: Int
    ): ImageAnalysis.Analyzer {
        val isFrontLens = lensFacing == CameraSelector.LENS_FACING_FRONT
        val analysisAIDB = AnalysisAIDB(viewfinderWidth, viewfinderHeight, isFrontLens)

        analysisAIDB.forwardCallback = { image: Bitmap -> aidbForward(image)}

        analysisAIDB.listener = object : AnalysisAIDB.Listener {
            override fun onAIDBFinished(aidbMeta: AIDBMeta,  bitmap: Bitmap) {
//                if(aidbMeta.time is null){}
                val msg = Message()
                msg.what = aidbMeta.time.toInt()
                handler.sendMessage(msg)
                boundsOverlay.post { boundsOverlay.drawBounds(aidbMeta) }
            }

            override fun onError(exception: Exception) {
                Log.d(TAG, "Face detection error", exception)
            }
        }
        return analysisAIDB
    }


    private fun arePermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestPermissions() {
        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (arePermissionsGranted()) {
                tryStartCamera()
            } else {
                Toast.makeText(this, R.string.permissions_not_granted, Toast.LENGTH_LONG).show()
                finish()
            }
        }
        launcher.launch(REQUIRED_PERMISSIONS)

    }


    override fun onDestroy() {
        super.onDestroy()

        if(1 == mMode) {
            // Shutdown analysis executor
            executor.shutdown()
            while (!executor.isShutdown) {
            }
        }
        // Unregister the display listener
        val manager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        manager.unregisterDisplayListener(displayListener)

        aidbReleaseInstance()
        Log.d(TAG, "====>> destory")
    }


}
