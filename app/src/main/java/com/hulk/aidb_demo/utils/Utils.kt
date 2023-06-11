package com.hulk.aidb_demo.utils
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import android.os.Environment
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.zip.ZipInputStream


inline infix fun Boolean.trueLet(trueBlock: Boolean.() -> Unit): Else {
    if (this) {
        trueBlock()
        return NotDoElse(this)
    }
    return DoElse(this)
}

inline infix fun Boolean.falseLet(falseBlock: Boolean.() -> Unit): Else {
    if (!this) {
        falseBlock()
        return NotDoElse(this)
    }
    return DoElse(this)
}

interface Else {
    infix fun elseLet(elseBlock: Boolean.() -> Unit)
}

class DoElse(private val boolean: Boolean) : Else {
    override infix fun elseLet(elseBlock: Boolean.() -> Unit) {
        elseBlock(boolean)
    }
}

class NotDoElse(private val boolean: Boolean) : Else {
    override infix fun elseLet(elseBlock: Boolean.() -> Unit) {
    }
}

fun rotateBitmap(origin: Bitmap, alpha: Float): Bitmap {

    val width: Int = origin.getWidth()
    val height: Int = origin.getHeight()
    val matrix = Matrix()
    matrix.setRotate(alpha)

    // 围绕原地进行旋转
    val newBM: Bitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
    if (newBM.equals(origin)) {
        return newBM
    }
    origin.recycle()
    return newBM
}

fun mirrorBitmap(origin: Bitmap): Bitmap {

    val width: Int = origin.getWidth()
    val height: Int = origin.getHeight()
    val matrix = Matrix()
    matrix.setScale(-1f, 1f)

    // 围绕原地进行旋转
    val newBM: Bitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true)
    if (newBM.equals(origin)) {
        return newBM
    }
    origin.recycle()
    return newBM
}

fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}

fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    val resizedBitmap = Bitmap.createBitmap(
        bm, 0, 0, width, height, matrix, false
    )
    bm.recycle()
    return resizedBitmap
}

/**
 * 创建文件夹
 */
fun mkdir(file: File) {
    if (file.exists()) {
        return
    } else {
        file.parentFile?.mkdir()
        file.mkdir()
    }
}

fun unzip(zipFilePath: String, desDirectory: String) {
    val desDir = File(desDirectory)
    if (!desDir.exists()) {
        val mkdirSuccess = desDir.mkdir();
        if (!mkdirSuccess) {
            throw Exception("创建解压目标文件夹失败")
        }
    }
    // 读入流（第二个参数，处理压缩文件中文异常。如果没有中文，可以不写第二个参数）
    val zipInputStream = ZipInputStream(FileInputStream(zipFilePath))
    // 遍历每一个文件
    var zipEntry = zipInputStream.nextEntry
    while (zipEntry != null) {
        println(zipEntry.toString())
        val unzipFilePath = desDirectory + File.separator + zipEntry.name
        if (zipEntry.isDirectory) {
            // 直接创建（注意，不是使用系统的mkdir,自定义方法）
            mkdir(File(unzipFilePath))
        } else {
            val file = File(unzipFilePath)
            if(file.exists())
                file.delete()
            // 创建父目录（注意，不是使用系统的mkdir,自定义方法）
            file.parentFile?.let { mkdir(it) }
            // 写出文件
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(unzipFilePath))
            val bytes = ByteArray(1024)
            var readLen: Int
            // Java 与 Kotlin的不同之处，需要特别关注。
            // while ((readLen = zipInputStream.read(bytes))!=-1){
            while (zipInputStream.read(bytes).also { readLen = it } > 0) {
                bufferedOutputStream.write(bytes, 0, readLen)
            }
            bufferedOutputStream.close()
        }
        zipInputStream.closeEntry()
        zipEntry = zipInputStream.nextEntry
    }
}

fun copyAssetAndWrite(fileName: String, dstPath: File, assets: AssetManager): Boolean {
    try {
//        val cacheDir = cacheDir
        if (!dstPath.exists()) {
            dstPath.mkdirs()
        }
        val outFile = File(dstPath, fileName)
        if (!outFile.exists()) {
            val res = outFile.createNewFile()
            if (!res) {
                return false
            }
        } else {
//                if (outFile.length() > 10) { //表示已经写入一次
//                    return true
//                }
            val res = outFile.delete()
            if (!res) {
                return false
            }
        }

        val `is` = assets.open(fileName)
        val fos = FileOutputStream(outFile)
        val buffer = ByteArray(1024)
        var byteCount: Int
        while (`is`.read(buffer).also { byteCount = it } != -1) {
            fos.write(buffer, 0, byteCount)
        }
        fos.flush()
        `is`.close()
        fos.close()
        return true
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}

fun Image.toBitmap(): Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride //像素个数，RGBA为4
    val rowStride = planes[0].rowStride //这里除pixelStride就是真实宽度
    val rowPadding = rowStride - pixelStride * width    //计算多余宽度
    val bitmap = Bitmap.createBitmap(
        width + rowPadding / pixelStride,
        height, Bitmap.Config.ARGB_8888
    )
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}

fun getFileMD5(file: File): String {
    if(!file.exists())
        return "file not existed!"
    val md = MessageDigest.getInstance("MD5")
    val inputStream = file.inputStream()

    val buffer = ByteArray(4096)
    var bytesRead = inputStream.read(buffer)

    while (bytesRead != -1) {
        md.update(buffer, 0, bytesRead)
        bytesRead = inputStream.read(buffer)
    }

    inputStream.close()

    val md5sum = BigInteger(1, md.digest()).toString(16)
    return md5sum.padStart(32, '0')
}

