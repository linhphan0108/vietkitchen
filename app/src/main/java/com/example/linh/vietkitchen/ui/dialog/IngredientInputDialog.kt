package com.example.linh.vietkitchen.ui.dialog

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.capWords
import com.example.linh.vietkitchen.ui.model.Ingredient
import kotlinx.android.synthetic.main.dialog_input_ingredient.*

class IngredientInputDialog : BottomSheetDialogFragment() {

    var listener: OnResultListeners? = null

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val width = ScreenUtil.screenWidth()
//        val height = ViewGroup.LayoutParams.WRAP_CONTENT
//        val dialog =  super.onCreateDialog(savedInstanceState)
//        dialog.window.setLayout(width, height)
//        return dialog
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_ingredient, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
        btnCancel.setOnClickListener { dismiss() }
        btnOk.setOnClickListener {
            if (listener != null && invalidateField()) {
                val name = edtName.text.toString().trim().capWords()
                val notes = edtNotes.text.toString().trim().capitalize()
                val quantity = edtQuantity.text.toString().trim().toInt()
                val unit = edtUnit.text.toString().trim()
                val ingredient = Ingredient(name, notes, quantity, unit)
                listener!!.onResult(ingredient)
            }
            dismiss()
        }
    }
//
//    override fun onResume() {
//        super.onResume()
//        val params = dialog.window!!.attributes
//        params.width = LayoutParams.MATCH_PARENT
//        params.height = LayoutParams.WRAP_CONTENT
//        dialog.window!!.attributes = params
//    }

    private fun invalidateField() : Boolean{
        return edtName.text.isNotBlank()
        && edtQuantity.text.isNotBlank() && edtUnit.text.isNotBlank()
    }

    interface OnResultListeners{
        fun onResult(ingredient: Ingredient)
    }
}