package com.example.linh.vietkitchen.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ExpandableListView
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.View
import android.view.animation.Transformation
import android.view.animation.TranslateAnimation
import android.widget.AbsListView


class SingleGroupOpenedExpandableListView : ExpandableListView{
    var lastGroupPosOpened: Int = 0
    var onGroupExpandCallback: OnGroupExpandListener? = null

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setOnGroupExpandListener {
            if (lastGroupPosOpened != it){
                collapseGroup(lastGroupPosOpened)
                lastGroupPosOpened = it
            }
            onGroupExpandCallback?.onGroupExpand(it)

        }
    }

    override fun expandGroup(groupPos: Int, animate: Boolean): Boolean {
        lastGroupPosOpened = groupPos
        return super.expandGroup(groupPos, animate)
    }


    fun expandOrCollapse(v: View, exp_or_colpse: String) {
        var anim: TranslateAnimation?
        if (exp_or_colpse == "expand") {
            anim = TranslateAnimation(0.0f, 0.0f, -v.height.toFloat(), 0.0f)
            v.visibility = View.VISIBLE
        } else {
            anim = TranslateAnimation(0.0f, 0.0f, 0.0f, -v.height.toFloat())
            val collapselistener = object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    v.visibility = View.GONE
                }
            }

            anim.setAnimationListener(collapselistener)
        }

        // To Collapse
        //

        anim.duration = 300
        anim.interpolator = AccelerateInterpolator(0.5f)
        v.startAnimation(anim)
    }

    fun expand(v: View) {
        v.measure(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            protected override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    AbsListView.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            protected override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }
}