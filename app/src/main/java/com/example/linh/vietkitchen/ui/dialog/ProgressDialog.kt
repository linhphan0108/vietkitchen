package com.example.linh.vietkitchen.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.dialog_progress.*

class ProgressDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_progress, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = false
        btnHide.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val width = (ScreenUtil.screenWidth() *0.8).toInt()
        val params = dialog.window!!.attributes
        params.width = width
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }

    fun updateProgress(totalFiles: Int, counter: Int, progress: Int){
        txtProgress.visibility = View.VISIBLE
        txtCounter.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        txtTitle.text = getString(R.string.uploading_image)
        txtProgress.text = getString(R.string.progress, progress)
        txtCounter.text = getString(R.string.progress_counter, counter, totalFiles)
        progressBar.progress = progress
    }

    fun updateMessage(msg: String){
        txtProgress.visibility = View.GONE
        txtCounter.visibility = View.GONE
        progressBar.visibility = View.GONE
        txtTitle.text = msg
    }
}