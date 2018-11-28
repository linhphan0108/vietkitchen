package com.example.linh.vietkitchen.ui.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import kotlinx.android.synthetic.main.dialog_loading.*

const val ARG_MESSAGE = "ARG_MESSAGE"

class LoadingDialog : DialogFragment() {
    companion object {
        fun newInstance(message: String): LoadingDialog{
            val dialog = LoadingDialog()
            return dialog.apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }

    lateinit var message: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_loading, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            message = it.getString(ARG_MESSAGE, "")
        }
        txtMessage.text = message
    }

    override fun onResume() {
        super.onResume()
        slackLoadingView.start()
    }

    override fun onPause() {
        super.onPause()
        slackLoadingView.start()
    }
}