package com.hulk.aidb_demo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.hulk.aidb.AIDBMeta
import com.hulk.aidb_demo.R

object VisualAiDB {
    private val coco_labels = listOf( "person", "bicycle", "car", "motorcycle", "airplane", "bus",
        "train", "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter",
        "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe",
        "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
        "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
        "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange",
        "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch", "potted plant", "bed",
        "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave",
        "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear",
        "hair drier", "toothbrush")
    private val movenet_line_rule = arrayOf(arrayOf(2, 1), arrayOf(2,  1), arrayOf(2, 4), arrayOf(1, 3), arrayOf(4, 0), arrayOf(0, 3),
        arrayOf(4,  6), arrayOf(3, 5), arrayOf(6, 8), arrayOf(8, 10), arrayOf(5, 7), arrayOf(7, 9),
        arrayOf(6,  12), arrayOf(5, 11), arrayOf(12, 11), arrayOf(12, 14), arrayOf(11, 13),
        arrayOf(14, 16), arrayOf(13, 15), arrayOf(2, 0), arrayOf(1, 0))

    private val colors = arrayOf(R.color.gold, R.color.hotpink, R.color.deeppink, R.color.red,
        R.color.salmon, R.color.darkkhaki, R.color.paleturquoise, R.color.greenyellow,
        R.color.darkorchid, R.color.darkred, R.color.skyblue, R.color.cyan,
        R.color.navy, R.color.blue, R.color.darkcyan, R.color.springgreen, R.color.indigo,
        R.color.maroon, R.color.red, R.color.mediumpurple)

    fun reMap(previewWidth: Int, previewHeight: Int,
               isFrontLens: Boolean,rotation: Int,
               imageProxy: ImageProxy, aidbMeta: AIDBMeta
    ){
        val reverseDimens = rotation == 90 || rotation == 270
        val width = if (reverseDimens) imageProxy.height else imageProxy.width
        val height = if (reverseDimens) imageProxy.width else imageProxy.height

        val scaleX = previewWidth / width.toFloat()
        val scaleY = previewHeight / height.toFloat()

        // face
        for (i in 0 until aidbMeta.face_num){
            // If the front camera lens is being used, reverse the right/left coordinates
            val flippedLeft = if (isFrontLens) width - aidbMeta.face_meta[i].x2 else aidbMeta.face_meta[i].x1
            val flippedRight = if (isFrontLens) width - aidbMeta.face_meta[i].x1 else aidbMeta.face_meta[i].x2

            aidbMeta.face_meta[i].x1 = scaleX * flippedLeft
            aidbMeta.face_meta[i].y1 *= scaleY
            aidbMeta.face_meta[i].x2 = scaleX * flippedRight
            aidbMeta.face_meta[i].y2 *= scaleY

            for (j in 0 until aidbMeta.face_meta[i].landmark_num){
                aidbMeta.face_meta[i].landmarks[2 * j] = if (isFrontLens) width - aidbMeta.face_meta[i].landmarks[2 * j] else aidbMeta.face_meta[i].landmarks[2 * j]
                aidbMeta.face_meta[i].landmarks[2 * j] *= scaleX
                aidbMeta.face_meta[i].landmarks[2 * j + 1] *= scaleY
            }
        }

        //object detection
        for (i in 0 until aidbMeta.object_num){
            // If the front camera lens is being used, reverse the right/left coordinates
            val flippedLeft = if (isFrontLens) width - aidbMeta.object_meta[i].x2 else aidbMeta.object_meta[i].x1
            val flippedRight = if (isFrontLens) width - aidbMeta.object_meta[i].x1 else aidbMeta.object_meta[i].x2

            aidbMeta.object_meta[i].x1 = scaleX * flippedLeft
            aidbMeta.object_meta[i].y1 *= scaleY
            aidbMeta.object_meta[i].x2 = scaleX * flippedRight
            aidbMeta.object_meta[i].y2 *= scaleY
        }

        // ocr
        for (i in 0 until aidbMeta.ocr_num){
            for(j in 0 until (aidbMeta.ocr_meta[i].points.size / 2)){
                // If the front camera lens is being used, reverse the right/left coordinates
                val flippedX = if (isFrontLens) width - aidbMeta.ocr_meta[i].points[2 * j] else aidbMeta.ocr_meta[i].points[2 * j]
                aidbMeta.ocr_meta[i].points[2 * j] = scaleX * flippedX
                aidbMeta.ocr_meta[i].points[2 * j + 1] *= scaleY

            }
        }

        if(aidbMeta.has_bitmap){
            val matrix = Matrix()
            if(aidbMeta.override){
                matrix.postScale(scaleX, scaleY)
            } else{
                matrix.postScale(512f / aidbMeta.bitmap.width.toFloat(),
                    512f / aidbMeta.bitmap.height.toFloat())
            }

            if (isFrontLens){
                matrix.preScale(-1.0f, 1.0f)
            }
            aidbMeta.bitmap = Bitmap.createBitmap(
                aidbMeta.bitmap, 0, 0,
                aidbMeta.bitmap.width, aidbMeta.bitmap.height, matrix, false
            )

        }
    }
    
    
    fun visual(context: Context, bitmap: Bitmap, aidbMeta: AIDBMeta, mirror: Boolean = false): Bitmap{

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, colors[(colors.indices).random()])
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val text_paint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.white)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val bitmap = if(mirror){
            mirrorBitmap(bitmap)
        } else{
            bitmap
        }

        val canvas = Canvas(bitmap)

        val width = bitmap.width
        val height = bitmap.height

        for(i in 0 until aidbMeta.face_num){

            val score = aidbMeta.face_meta[i].conf
            val show_text = "conf:%.2f".format(score)
            val text_size = paint.measureText(show_text)

            val left = if (mirror) width - aidbMeta.face_meta[i].x2 else aidbMeta.face_meta[i].x1
            val right = if (mirror) width - aidbMeta.face_meta[i].x1 else aidbMeta.face_meta[i].x2

//            val rect = RectF(aidbMeta.face_meta[i].x1, aidbMeta.face_meta[i].y1,
//                aidbMeta.face_meta[i].x2, aidbMeta.face_meta[i].y2
//            )
            val rect = RectF(left, aidbMeta.face_meta[i].y1,
                right, aidbMeta.face_meta[i].y2
            )
            canvas.drawRect(rect, paint)

//            val rect2 = RectF(aidbMeta.face_meta[i].x1 - paint.strokeWidth / 2,
//                aidbMeta.face_meta[i].y1 - paint.textSize - paint.strokeWidth,
//                aidbMeta.face_meta[i].x1 + text_size + paint.strokeWidth / 2,
//                aidbMeta.face_meta[i].y1
//            )
            val rect2 = RectF(left - paint.strokeWidth / 2,
                aidbMeta.face_meta[i].y1 - paint.textSize - paint.strokeWidth,
                left + text_size + paint.strokeWidth / 2,
                aidbMeta.face_meta[i].y1
            )
            canvas.drawRect(rect2, paint)

            canvas.drawText(show_text,
                rect.left + paint.strokeWidth / 2 + text_size / 2,
                rect.top - paint.strokeWidth,
                text_paint)

            // movenet
            if(17 == aidbMeta.face_meta[i].landmark_num){
                for((start, end) in movenet_line_rule){
//                    canvas.drawLine(aidbMeta.face_meta[i].landmarks[2 * start], aidbMeta.face_meta[i].landmarks[2 * start + 1],
//                        aidbMeta.face_meta[i].landmarks[2 * end], aidbMeta.face_meta[i].landmarks[2 * end + 1],
//                        paint)
                    canvas.drawLine(if(mirror) width - aidbMeta.face_meta[i].landmarks[2 * start] else aidbMeta.face_meta[i].landmarks[2 * start],
                        aidbMeta.face_meta[i].landmarks[2 * start + 1],
                        if(mirror) width - aidbMeta.face_meta[i].landmarks[2 * end] else aidbMeta.face_meta[i].landmarks[2 * end],
                        aidbMeta.face_meta[i].landmarks[2 * end + 1],
                        paint)
                }
            } else{
                for (j in 0 until aidbMeta.face_meta[i].landmark_num){
//                    canvas.drawCircle(aidbMeta.face_meta[i].landmarks[2 * j], aidbMeta.face_meta[i].landmarks[2 * j + 1],
//                        5F, paint)
                    canvas.drawCircle(if(mirror) width - aidbMeta.face_meta[i].landmarks[2 * j] else aidbMeta.face_meta[i].landmarks[2 * j],
                        aidbMeta.face_meta[i].landmarks[2 * j + 1],
                        5F, paint)
                }
            }


        }

        for(i in 0 until aidbMeta.object_num){

//            val score = aidbMeta.object_meta[i].conf
            val label = aidbMeta.object_meta[i].label
            val show_text = coco_labels[label]

            val text_size = paint.measureText(show_text)

            val left = if (mirror) width - aidbMeta.object_meta[i].x2 else aidbMeta.object_meta[i].x1
            val right = if (mirror) width - aidbMeta.object_meta[i].x1 else aidbMeta.object_meta[i].x2

            val rect = RectF(left, aidbMeta.object_meta[i].y1,
                right, aidbMeta.object_meta[i].y2
            )

//            val rect = RectF(aidbMeta.object_meta[i].x1, aidbMeta.object_meta[i].y1,
//                aidbMeta.object_meta[i].x2, aidbMeta.object_meta[i].y2
//            )
            canvas.drawRect(rect, paint)

            val rect2 = RectF(left - paint.strokeWidth / 2,
                aidbMeta.object_meta[i].y1 - paint.textSize - paint.strokeWidth,
                left + text_size + paint.strokeWidth / 2,
                aidbMeta.object_meta[i].y1
            )

//            val rect2 = RectF(aidbMeta.object_meta[i].x1 - paint.strokeWidth / 2,
//                aidbMeta.object_meta[i].y1 - paint.textSize - paint.strokeWidth,
//                aidbMeta.object_meta[i].x1 + text_size + paint.strokeWidth / 2,
//                aidbMeta.object_meta[i].y1
//            )
            canvas.drawRect(rect2, paint)

            canvas.drawText(show_text,
                rect.left + paint.strokeWidth / 2 + text_size / 2,
                rect.top - paint.strokeWidth,
                text_paint)


        }

        for(i in 0 until aidbMeta.ocr_num){
            var topIndex = 0
            var bottomIndex = 0
            var curX = 0.0f
            var minY = bitmap.height.toFloat()
            var maxY = 0.0f
            for(j in 0 until aidbMeta.ocr_meta[i].points.size / 2){
                if(aidbMeta.ocr_meta[i].points[2 * j + 1] < minY){
                    bottomIndex = j
                    minY = aidbMeta.ocr_meta[i].points[2 * j + 1]
                    curX = aidbMeta.ocr_meta[i].points[2 * j]
                } else if(aidbMeta.ocr_meta[i].points[2 * j + 1] == minY){
                    if(aidbMeta.ocr_meta[i].points[2 * j] < curX){
                        bottomIndex = j
                        minY = aidbMeta.ocr_meta[i].points[2 * j + 1]
                        curX = aidbMeta.ocr_meta[i].points[2 * j]
                    }
                }
                if(aidbMeta.ocr_meta[i].points[2 * j + 1] > maxY){
                    topIndex = j
                    maxY = aidbMeta.ocr_meta[i].points[2 * j + 1]
                    curX = aidbMeta.ocr_meta[i].points[2 * j]
                } else if(aidbMeta.ocr_meta[i].points[2 * j + 1] == maxY){
                    if(aidbMeta.ocr_meta[i].points[2 * j] > curX) {
                        topIndex = j
                        maxY = aidbMeta.ocr_meta[i].points[2 * j + 1]
                        curX = aidbMeta.ocr_meta[i].points[2 * j]
                    }
                }
            }

            var index1 = 0
            for(j in 0 until 4){
                if(j != topIndex && j != bottomIndex){
                    index1 = j
                    break
                }
            }
            // assume 4 points
            val index2 = 6 - index1 - topIndex - bottomIndex

            canvas.drawLine(if(mirror) width - aidbMeta.ocr_meta[i].points[2 * topIndex] else aidbMeta.ocr_meta[i].points[2 * topIndex],
                aidbMeta.ocr_meta[i].points[2 * topIndex + 1],
                if(mirror) width - aidbMeta.ocr_meta[i].points[2 * index1] else aidbMeta.ocr_meta[i].points[2 * index1],
                aidbMeta.ocr_meta[i].points[2 * index1 + 1], paint)

            canvas.drawLine(if(mirror) width - aidbMeta.ocr_meta[i].points[2 * topIndex] else aidbMeta.ocr_meta[i].points[2 * topIndex],
                aidbMeta.ocr_meta[i].points[2 * topIndex + 1],
                if(mirror) width -  aidbMeta.ocr_meta[i].points[2 * index2] else aidbMeta.ocr_meta[i].points[2 * index2],
                aidbMeta.ocr_meta[i].points[2 * index2 + 1], paint)

            canvas.drawLine(if(mirror) width - aidbMeta.ocr_meta[i].points[2 * bottomIndex] else aidbMeta.ocr_meta[i].points[2 * bottomIndex],
                aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1],
                if(mirror) width - aidbMeta.ocr_meta[i].points[2 * index1] else aidbMeta.ocr_meta[i].points[2 * index1],
                aidbMeta.ocr_meta[i].points[2 * index1 + 1], paint)

            canvas.drawLine(if(mirror) width - aidbMeta.ocr_meta[i].points[2 * bottomIndex] else aidbMeta.ocr_meta[i].points[2 * bottomIndex],
                aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1],
                if(mirror) width -  aidbMeta.ocr_meta[i].points[2 * index2] else aidbMeta.ocr_meta[i].points[2 * index2],
                aidbMeta.ocr_meta[i].points[2 * index2 + 1], paint)

//            canvas.drawLine(aidbMeta.ocr_meta[i].points[2 * topIndex], aidbMeta.ocr_meta[i].points[2 * topIndex + 1],
//                aidbMeta.ocr_meta[i].points[2 * index1], aidbMeta.ocr_meta[i].points[2 * index1 + 1], paint)
//            canvas.drawLine(aidbMeta.ocr_meta[i].points[2 * topIndex], aidbMeta.ocr_meta[i].points[2 * topIndex + 1],
//                aidbMeta.ocr_meta[i].points[2 * index2], aidbMeta.ocr_meta[i].points[2 * index2 + 1], paint)
//
//            canvas.drawLine(aidbMeta.ocr_meta[i].points[2 * bottomIndex], aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1],
//                aidbMeta.ocr_meta[i].points[2 * index1], aidbMeta.ocr_meta[i].points[2 * index1 + 1], paint)
//            canvas.drawLine(aidbMeta.ocr_meta[i].points[2 * bottomIndex], aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1],
//                aidbMeta.ocr_meta[i].points[2 * index2], aidbMeta.ocr_meta[i].points[2 * index2 + 1], paint)

            val text = aidbMeta.ocr_meta[i].text
            val text_size = paint.measureText(text)

//            canvas.drawText(text,
//                aidbMeta.ocr_meta[i].points[2 * bottomIndex] + paint.strokeWidth / 2 + text_size / 2,
//                aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1] - paint.strokeWidth,
//                text_paint)
            canvas.drawText(text,
                if(mirror) width - aidbMeta.ocr_meta[i].points[2 * bottomIndex] - text_size else aidbMeta.ocr_meta[i].points[2 * bottomIndex] + paint.strokeWidth / 2 + text_size / 2,
                aidbMeta.ocr_meta[i].points[2 * bottomIndex + 1] - paint.strokeWidth,
                text_paint)


        }

        if(aidbMeta.label != null){

            val text = aidbMeta.label
            val conf = "conf:%.2f".format(aidbMeta.conf)
            val text_size = paint.measureText(text)
            canvas.drawText(text,
                paint.strokeWidth / 2 + text_size / 2,
                paint.strokeWidth + text_size / text.length,
                text_paint)
            canvas.drawText(conf,
                paint.strokeWidth / 2 + text_size / 2,
                2 * (paint.strokeWidth * 2 + text_size / text.length),
                text_paint)
        }

        if(aidbMeta.has_bitmap){
            return if(mirror){
                mirrorBitmap(aidbMeta.bitmap)
            } else{
                aidbMeta.bitmap
            }
        }

       return bitmap

    }

    fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, false
        )
    }
}