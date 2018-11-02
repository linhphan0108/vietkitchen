package com.example.linh.vietkitchen.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.Bitmap
import android.support.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.*
import android.graphics.BitmapFactory
import timber.log.Timber


const val IMAGE_COMPRESSION_DIR = "/ImageCompressor"
const val MAX_IMAGE_WIDTH = 1080
const val MAX_IMAGE_HEIGHT = 810 // w/h = 4/3
const val MAX_THUMB_IMAGE_WIDTH = 540 // ~0.5 MAX WIDTH
const val MAX_THUMB_IMAGE_HEIGHT = 405

class ImageOptimizationUtil {
    /**
     * compress the file/photo from @param <b>uri</b> to a private location on the current device and return the compressed file.
        @param uri = The original image uri
        @param context = Current android Context
     */

    fun optimizeThumbImage(context: Context, uri: Uri, maxWidth: Int = MAX_THUMB_IMAGE_WIDTH, maxHeight: Int = MAX_THUMB_IMAGE_HEIGHT): File {
        return optimize(context, uri, maxWidth, maxHeight)
    }

    @Throws(IOException::class)
    fun optimize(context: Context, uri: Uri, maxWidth: Int = MAX_IMAGE_WIDTH, maxHeight: Int = MAX_IMAGE_HEIGHT): File {
        //decode and resize the original bitmap from @param uri.
        val scaledBitmap = scaleImageFromFiles(context, uri, maxWidth, maxHeight)

        var out: FileOutputStream? = null
        val filepath = createCacheFile(context)//create placeholder for the compressed image file
        try {
            out = FileOutputStream(filepath)
            //write the compressed bitmap at the destination specified by filename.
            val result = scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            if (result!!){
                Timber.d("a scaled image saved at $filepath")
            }else{
                Timber.d("failed to created and saved a scaled image")
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }finally {
            scaledBitmap?.recycle()
            out?.close()
        }
        return filepath
    }

    private fun scaleImageFromFiles(context: Context, imageUri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
//        var bmp = BitmapFactory.decodeFile(imageUri, options)
        var bmp: Bitmap? = getBitmap(context, imageUri, options)

        val actualHeight = options.outHeight
        val actualWidth = options.outWidth
        val imgRatio = actualWidth.toFloat() / actualHeight
        val maxRatio = maxWidth.toFloat() / maxHeight
        var optHeight = actualHeight
        var optWidth = actualWidth
        var optRatio = imgRatio

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> { // actualHeight > maxHeight & actualWidth < maxWidth
                    optRatio = maxHeight.toFloat() / actualHeight
                    optWidth = (optRatio * actualWidth).toInt()
                    optHeight = maxHeight
                }
                imgRatio > maxRatio -> {// actualHeight < maxHeight & actualWidth > maxWidth
                    optRatio = maxWidth.toFloat() / actualWidth
                    optHeight = (optRatio * actualHeight).toInt()
                    optWidth = maxWidth
                }
                else -> {//actualHeight > maxHeight & actualWidth > maxWidth & imgRatio = maxRatio
                    optHeight = maxHeight
                    optWidth = maxWidth
                }
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, optWidth, optHeight)
        options.inJustDecodeBounds = false //inJustDecodeBounds set to false to load the actual bitmap
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16384)//16*1024

        try {
//            bmp = BitmapFactory.decodeFile(imageUri, options)
            bmp = getBitmap(context, imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(optWidth, optHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = optWidth / options.outWidth.toFloat()
        val ratioY = optHeight / options.outHeight.toFloat()
        val middleX = optWidth / 2.0f
        val middleY = optHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp, middleX - bmp!!.width / 2, middleY - bmp.height / 2, Paint(FILTER_BITMAP_FLAG))
        bmp.recycle()

        //check the rotation of the image and display it properly
        try {
            val exif = getExif(context, imageUri)
            val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap!!.width, scaledBitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return scaledBitmap

        // decode with the sample size
//        var scale = 1
//        while (options.outWidth / scale / 2 >= maxWidth && options.outHeight / scale / 2 >= maxHeight) {
//            scale *= 2
//        }
//        options.inSampleSize = scale
//        options.inJustDecodeBounds = false
//        return BitmapFactory.decodeFile(imageUri, options)
    }

    private fun getExif(context: Context, imageUri: Uri): ExifInterface? {
        var exif: ExifInterface? = null
//        if (Build.VERSION.SDK_INT >= 24){
            var input: InputStream? = null
            try {
                input = context.contentResolver.openInputStream(imageUri)
                exif = ExifInterface(input)
            }catch (e: IOException){
                Timber.e(e)
            }finally {
                input?.close()
            }
//        }else{
//            val realPath = fileUtil.getRealPathFromURI(context, imageUri)
//            Timber.d("real image's path $realPath")
//            return ExifInterface(realPath)
//        }
        return exif
    }

    private fun createCacheFile(context: Context): File {
        val imgCompressionDirFile = getCacheCompressionDir(context)
        return File(imgCompressionDirFile, "${System.currentTimeMillis()}.jpg")
    }

    private fun getCacheCompressionDir(context: Context): File{
        //getting device external cache directory, might not be available on some devices,
        // so our code fall back to internal storage cache directory, which is always available but in smaller quantity
        var cacheDir = context.externalCacheDir
        if (cacheDir == null)//fallback
            cacheDir = context.cacheDir
        val imgCompressionDirFile = File(cacheDir, IMAGE_COMPRESSION_DIR)
        //Create ImageCompressor folder if it doesn't already exists.
        if (!imgCompressionDirFile.exists())
            imgCompressionDirFile.mkdirs()
        return imgCompressionDirFile
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height
        val totalReqPixelsCap = reqWidth * reqHeight * 2

        while (totalPixels.toFloat() / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    private fun getBitmap(context: Context, uri: Uri, options: BitmapFactory.Options): Bitmap?{
        var bitmap: Bitmap ?= null
        val cr = context.contentResolver
        try {
            val inputStream = cr.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            // close stream
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }catch (e: FileNotFoundException){}
        return bitmap
    }

    private fun getBitmap(cr: ContentResolver, uri: Uri){
        val bitmap = MediaStore.Images.Media.getBitmap(cr, uri)
    }
}