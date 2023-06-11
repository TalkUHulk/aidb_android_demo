package com.hulk.aidb_demo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hulk.aidb.AIDBMeta
import com.hulk.aidb_demo.R

/** Overlay where face bounds are drawn.  */
class BoundsOverlay constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

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

    private var frameMeta: AIDBMeta = AIDBMeta()
    private val paint_list = listOf(
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.gold)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.hotpink)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },  Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.deeppink)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.red)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.salmon)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.darkkhaki)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.paleturquoise)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.greenyellow)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.darkorchid)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.darkred)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.skyblue)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.cyan)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.navy)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.blue)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.darkcyan)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.springgreen)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.indigo)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.maroon)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.red)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.mediumpurple)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        })

    private val paint_hat_list = listOf(
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.gold)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.hotpink)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },  Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.deeppink)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.red)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.salmon)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.darkkhaki)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.paleturquoise)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }, Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.greenyellow)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.darkorchid)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.darkred)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.skyblue)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.cyan)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.navy)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.blue)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.darkcyan)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.springgreen)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.indigo)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.maroon)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.red)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        },
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context!!, R.color.mediumpurple)
            strokeWidth = 10f
            textSize = 50f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        })

    val text_paint = Paint().apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context!!, R.color.white)
        strokeWidth = 10f
        textSize = 50f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(i in 0 until frameMeta.face_num){

            val score = frameMeta.face_meta[i].conf
            val show_text = "conf:%.2f".format(score)
            val label = frameMeta.face_num % paint_list.size
            val text_size = paint_list[label].measureText(show_text)

            val rect = RectF(frameMeta.face_meta[i].x1, frameMeta.face_meta[i].y1,
                frameMeta.face_meta[i].x2, frameMeta.face_meta[i].y2
            )
            canvas.drawRect(rect, paint_list[label])

            val rect2 = RectF(frameMeta.face_meta[i].x1 - paint_list[label].strokeWidth / 2,
                frameMeta.face_meta[i].y1 - paint_list[label].textSize - paint_list[label].strokeWidth,
                frameMeta.face_meta[i].x1 + text_size + paint_list[label].strokeWidth / 2,
                frameMeta.face_meta[i].y1
            )
            canvas.drawRect(rect2, paint_hat_list[label])

            canvas.drawText(show_text,
                rect.left + paint_list[label].strokeWidth / 2 + text_size / 2,
                rect.top - paint_list[label].strokeWidth,
                text_paint)

            //movenet
            if(17 == frameMeta.face_meta[i].landmark_num){
                for((start, end) in movenet_line_rule){
                    canvas.drawLine(frameMeta.face_meta[i].landmarks[2 * start], frameMeta.face_meta[i].landmarks[2 * start + 1],
                        frameMeta.face_meta[i].landmarks[2 * end], frameMeta.face_meta[i].landmarks[2 * end + 1],
                        paint_hat_list[frameMeta.face_meta[i].landmark_num % paint_hat_list.size])
                }
            } else{
                for (j in 0 until frameMeta.face_meta[i].landmark_num){
                    canvas.drawCircle(frameMeta.face_meta[i].landmarks[2 * j], frameMeta.face_meta[i].landmarks[2 * j + 1],
                        5F, paint_hat_list[j % paint_hat_list.size])
                }
            }


        }

        for(i in 0 until frameMeta.object_num){

//            val score = frameMeta.object_meta[i].conf
            val label = frameMeta.object_meta[i].label
            val paint_label = label % paint_list.size
            val show_text = coco_labels[label]

            val text_size = paint_list[paint_label].measureText(show_text)

            val rect = RectF(frameMeta.object_meta[i].x1, frameMeta.object_meta[i].y1,
                frameMeta.object_meta[i].x2, frameMeta.object_meta[i].y2
            )
            canvas.drawRect(rect, paint_list[paint_label])

            val rect2 = RectF(frameMeta.object_meta[i].x1 - paint_list[paint_label].strokeWidth / 2,
                frameMeta.object_meta[i].y1 - paint_list[paint_label].textSize - paint_list[paint_label].strokeWidth,
                frameMeta.object_meta[i].x1 + text_size + paint_list[paint_label].strokeWidth / 2,
                frameMeta.object_meta[i].y1
            )
            canvas.drawRect(rect2, paint_hat_list[paint_label])

            canvas.drawText(show_text,
                rect.left + paint_list[paint_label].strokeWidth / 2 + text_size / 2,
                rect.top - paint_list[paint_label].strokeWidth,
                text_paint)


        }

        for(i in 0 until frameMeta.ocr_num){
            var topIndex = 0
            var bottomIndex = 0
            var curX = 0.0f
            var minY = height.toFloat()
            var maxY = 0.0f
            for(j in 0 until frameMeta.ocr_meta[i].points.size / 2){
                if(frameMeta.ocr_meta[i].points[2 * j + 1] < minY){
                    bottomIndex = j
                    minY = frameMeta.ocr_meta[i].points[2 * j + 1]
                    curX = frameMeta.ocr_meta[i].points[2 * j]
                } else if(frameMeta.ocr_meta[i].points[2 * j + 1] == minY){
                    if(frameMeta.ocr_meta[i].points[2 * j] < curX){
                        bottomIndex = j
                        minY = frameMeta.ocr_meta[i].points[2 * j + 1]
                        curX = frameMeta.ocr_meta[i].points[2 * j]
                    }
                }
                if(frameMeta.ocr_meta[i].points[2 * j + 1] > maxY){
                    topIndex = j
                    maxY = frameMeta.ocr_meta[i].points[2 * j + 1]
                    curX = frameMeta.ocr_meta[i].points[2 * j]
                } else if(frameMeta.ocr_meta[i].points[2 * j + 1] == maxY){
                    if(frameMeta.ocr_meta[i].points[2 * j] > curX) {
                        topIndex = j
                        maxY = frameMeta.ocr_meta[i].points[2 * j + 1]
                        curX = frameMeta.ocr_meta[i].points[2 * j]
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

            canvas.drawLine(frameMeta.ocr_meta[i].points[2 * topIndex], frameMeta.ocr_meta[i].points[2 * topIndex + 1],
                frameMeta.ocr_meta[i].points[2 * index1], frameMeta.ocr_meta[i].points[2 * index1 + 1], paint_hat_list[i % paint_hat_list.size])
            canvas.drawLine(frameMeta.ocr_meta[i].points[2 * topIndex], frameMeta.ocr_meta[i].points[2 * topIndex + 1],
                frameMeta.ocr_meta[i].points[2 * index2], frameMeta.ocr_meta[i].points[2 * index2 + 1], paint_hat_list[i % paint_hat_list.size])

            canvas.drawLine(frameMeta.ocr_meta[i].points[2 * bottomIndex], frameMeta.ocr_meta[i].points[2 * bottomIndex + 1],
                frameMeta.ocr_meta[i].points[2 * index1], frameMeta.ocr_meta[i].points[2 * index1 + 1], paint_hat_list[i % paint_hat_list.size])
            canvas.drawLine(frameMeta.ocr_meta[i].points[2 * bottomIndex], frameMeta.ocr_meta[i].points[2 * bottomIndex + 1],
                frameMeta.ocr_meta[i].points[2 * index2], frameMeta.ocr_meta[i].points[2 * index2 + 1], paint_list[i % paint_list.size])

            val text = frameMeta.ocr_meta[i].text
            val text_size = paint_list[i % paint_list.size].measureText(text)

            canvas.drawText(text,
                frameMeta.ocr_meta[i].points[2 * bottomIndex] + paint_list[i % paint_list.size].strokeWidth / 2 + text_size / 2,
                frameMeta.ocr_meta[i].points[2 * bottomIndex + 1] - paint_list[i % paint_list.size].strokeWidth,
                text_paint)


        }

        if(frameMeta.label != null){

            val text = frameMeta.label
            val conf = "conf:%.2f".format(frameMeta.conf)
            val text_size = paint_list[0].measureText(text)
            canvas.drawText(text,
                paint_list[0].strokeWidth / 2 + text_size / 2,
                paint_list[0].strokeWidth + text_size / text.length,
                text_paint)
            canvas.drawText(conf,
                paint_list[0].strokeWidth / 2 + text_size / 2,
                2 * (paint_list[0].strokeWidth * 2 + text_size / text.length),
                text_paint)
        }

        if(frameMeta.has_bitmap){
            val paint = Paint()
            canvas.drawBitmap(frameMeta.bitmap, 0f, 0f, paint)
        }

    }

    fun drawBounds(frameMeta: AIDBMeta) {
        this.frameMeta = frameMeta
        invalidate()
    }


}