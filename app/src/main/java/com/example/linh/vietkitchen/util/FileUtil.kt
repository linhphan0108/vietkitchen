package com.example.linh.vietkitchen.util

import android.provider.MediaStore
import android.provider.DocumentsContract
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.net.Uri


class FileUtil {
    /**
     * Gets the real path from file
     * @param context
     * @param contentUri
     * @return path
     */
    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getPathForV19AndUp(context, contentUri)
        } else {
            getPathForPreV19(context, contentUri)
        }
    }

    /**
     * Handles pre V19 uri's
     * @param context
     * @param contentUri
     * @return
     */
    fun getPathForPreV19(context: Context, contentUri: Uri): String? {
        var res: String? = null

        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()

        return res
    }

    /**
     * Handles V19 and up uri's
     * @param context
     * @param contentUri
     * @return path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPathForV19AndUp(context: Context, contentUri: Uri): String {
        val wholeID = DocumentsContract.getDocumentId(contentUri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)

        var filePath = ""
        val columnIndex = cursor.getColumnIndex(column[0])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }

        cursor.close()
        return filePath
    }
}