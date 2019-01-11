package com.example.linh.vietkitchen.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import kotlinx.android.synthetic.main.bottom_sheet_options.*

class BottomSheetOptions: BottomSheetDialogFragment() {
    var listeners: BottomSheetOptionsListeners? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.bottom_sheet_options, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnDelete.setOnClickListener {
            listeners?.onDelete()
            dismiss()
        }
    }

    interface BottomSheetOptionsListeners{
        fun onDelete()
    }
}