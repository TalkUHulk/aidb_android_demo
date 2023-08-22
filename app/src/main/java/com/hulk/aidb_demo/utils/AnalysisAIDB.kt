package com.hulk.aidb_demo.utils

import android.annotation.SuppressLint
import android.graphics.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.hulk.aidb.AIDBMeta
import kotlin.system.measureTimeMillis


/** CameraX Analyzer that wraps MLKit's FaceDetector.  */
internal class AnalysisAIDB(
    private val previewWidth: Int,
    private val previewHeight: Int,
    private val isFrontLens: Boolean,
) : ImageAnalysis.Analyzer {

    /** Listener to receive callbacks for when faces are detected, or an error occurs.  */
    var listener: Listener? = null
//    var imageView: ImageView ? = null
    lateinit var forwardCallback: ((Bitmap)->AIDBMeta)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError", "SdCardPath")
    override fun analyze(imageProxy: ImageProxy) {

        val image = imageProxy.image
        if (image == null) {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees


        val rotateBitmap = rotateBitmap(image.toBitmap(), rotation.toFloat())
        var aidbMeta: AIDBMeta
        val time = measureTimeMillis {
            aidbMeta = forwardCallback(rotateBitmap)
        }
//        if(aidbMeta.error_code != 0) return
//        Log.d("TimeCost", "======>>>${time}")
        val listener = listener
        
        VisualAiDB.reMap(previewWidth, previewHeight, isFrontLens, rotation, imageProxy, aidbMeta)
//        val reverseDimens = rotation == 90 || rotation == 270
//        val width = if (reverseDimens) imageProxy.height else imageProxy.width
//        val height = if (reverseDimens) imageProxy.width else imageProxy.height
//
//        val scaleX = previewWidth / width.toFloat()
//        val scaleY = previewHeight / height.toFloat()
//
//        // face
//        for (i in 0 until aidbMeta.face_num){
//            // If the front camera lens is being used, reverse the right/left coordinates
//            val flippedLeft = if (isFrontLens) width - aidbMeta.face_meta[i].x2 else aidbMeta.face_meta[i].x1
//            val flippedRight = if (isFrontLens) width - aidbMeta.face_meta[i].x1 else aidbMeta.face_meta[i].x2
//
//            aidbMeta.face_meta[i].x1 = scaleX * flippedLeft
//            aidbMeta.face_meta[i].y1 *= scaleY
//            aidbMeta.face_meta[i].x2 = scaleX * flippedRight
//            aidbMeta.face_meta[i].y2 *= scaleY
//
//            for (j in 0 until aidbMeta.face_meta[i].landmark_num){
//                aidbMeta.face_meta[i].landmarks[2 * j] = if (isFrontLens) width - aidbMeta.face_meta[i].landmarks[2 * j] else aidbMeta.face_meta[i].landmarks[2 * j]
//                aidbMeta.face_meta[i].landmarks[2 * j] *= scaleX
//                aidbMeta.face_meta[i].landmarks[2 * j + 1] *= scaleY
//            }
//        }
//
//        //object detection
//        for (i in 0 until aidbMeta.object_num){
//            // If the front camera lens is being used, reverse the right/left coordinates
//            val flippedLeft = if (isFrontLens) width - aidbMeta.object_meta[i].x2 else aidbMeta.object_meta[i].x1
//            val flippedRight = if (isFrontLens) width - aidbMeta.object_meta[i].x1 else aidbMeta.object_meta[i].x2
//
//            aidbMeta.object_meta[i].x1 = scaleX * flippedLeft
//            aidbMeta.object_meta[i].y1 *= scaleY
//            aidbMeta.object_meta[i].x2 = scaleX * flippedRight
//            aidbMeta.object_meta[i].y2 *= scaleY
//        }
//
//        // ocr
//        for (i in 0 until aidbMeta.ocr_num){
//            for(j in 0 until (aidbMeta.ocr_meta[i].points.size / 2)){
//                // If the front camera lens is being used, reverse the right/left coordinates
//                val flippedX = if (isFrontLens) width - aidbMeta.ocr_meta[i].points[2 * j] else aidbMeta.ocr_meta[i].points[2 * j]
//                aidbMeta.ocr_meta[i].points[2 * j] = scaleX * flippedX
//                aidbMeta.ocr_meta[i].points[2 * j + 1] *= scaleY
//
//            }
//        }
//
//        if(aidbMeta.has_bitmap){
//            val matrix = Matrix()
//            if(aidbMeta.override){
//                matrix.postScale(scaleX, scaleY)
//            } else{
//                matrix.postScale(512f / aidbMeta.bitmap.width.toFloat(),
//                    512f / aidbMeta.bitmap.height.toFloat())
//            }
//
//            if (isFrontLens){
//                matrix.preScale(-1.0f, 1.0f)
//            }
//            aidbMeta.bitmap = Bitmap.createBitmap(
//                aidbMeta.bitmap, 0, 0,
//                aidbMeta.bitmap.width, aidbMeta.bitmap.height, matrix, false
//            )
//
//        }


        listener?.onAIDBFinished(aidbMeta, rotateBitmap)
        imageProxy.close()

    }

//    private fun Image.toBitmap(): Bitmap {
//        val buffer: ByteBuffer = planes[0].buffer
//        val pixelStride = planes[0].pixelStride //像素个数，RGBA为4
//        val rowStride = planes[0].rowStride //这里除pixelStride就是真实宽度
//        val rowPadding = rowStride - pixelStride * width    //计算多余宽度
//        val bitmap = Bitmap.createBitmap(
//            width + rowPadding / pixelStride,
//            height, Bitmap.Config.ARGB_8888
//        )
//        bitmap.copyPixelsFromBuffer(buffer)
//        return bitmap
//    }

    private fun Rect.transform(width: Int, height: Int): RectF {

        val scaleX = previewWidth / width.toFloat()
        val scaleY = previewHeight / height.toFloat()

        // If the front camera lens is being used, reverse the right/left coordinates
        val flippedLeft = if (isFrontLens) width - right else left
        val flippedRight = if (isFrontLens) width - left else right

        // Scale all coordinates to match preview
        val scaledLeft = scaleX * flippedLeft
        val scaledTop = scaleY * top
        val scaledRight = scaleX * flippedRight
        val scaledBottom = scaleY * bottom
        return RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
    }

    private fun RectF.transform(width: Int, height: Int): RectF {

        val scaleX = previewWidth / width.toFloat()
        val scaleY = previewHeight / height.toFloat()

        // If the front camera lens is being used, reverse the right/left coordinates
        val flippedLeft = if (isFrontLens) width - right else left
        val flippedRight = if (isFrontLens) width - left else right

        // Scale all coordinates to match preview
        val scaledLeft = scaleX * flippedLeft
        val scaledTop = scaleY * top
        val scaledRight = scaleX * flippedRight
        val scaledBottom = scaleY * bottom
        return RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
    }

    /**
     * Interface to register callbacks for when the face detector provides detected face bounds, or
     * when it encounters an error.
     */
    internal interface Listener {
        /** Callback that receives hand bounds that can be drawn on top of the viewfinder.  */
        fun onAIDBFinished(aidbMeta: AIDBMeta, bitmap: Bitmap)
        /** Invoked when an error is encounter during face detection.  */
        fun onError(exception: Exception)
    }

}