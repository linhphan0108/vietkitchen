package com.example.linh.vietkitchen.ui.screen.home

import android.content.Context
import android.graphics.Canvas
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.View.MeasureSpec
import android.widget.ExpandableListView
import android.view.ViewGroup
import android.util.SparseArray
import android.widget.BaseExpandableListAdapter
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Transformation
import android.widget.AbsListView.LayoutParams
import com.example.linh.vietkitchen.ui.custom.AnimatedExpandableListView


/**
 * A specialized adapter for use with the AnimatedExpandableListView. All
 * adapters used with AnimatedExpandableListView MUST extend this class.
 */
abstract class AnimatedExpandableListAdapter : BaseExpandableListAdapter() {
    private val groupInfo = SparseArray<GroupInfo>()
    var parent: AnimatedExpandableListView? = null

    private val realChildTypeCount: Int
        get() = 1

    private fun getRealChildType(groupPosition: Int, childPosition: Int): Int {
        return 0
    }

    abstract fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View
    abstract fun getRealChildrenCount(groupPosition: Int): Int

    private fun getGroupInfo(groupPosition: Int): GroupInfo {
        var info: GroupInfo? = groupInfo.get(groupPosition)
        if (info == null) {
            info = GroupInfo()
            groupInfo.put(groupPosition, info)
        }
        return info
    }

    fun notifyGroupExpanded(groupPosition: Int) {
        val info = getGroupInfo(groupPosition)
        info.dummyHeight = -1
    }

    fun startExpandAnimation(groupPosition: Int, firstChildPosition: Int) {
        val info = getGroupInfo(groupPosition)
        info.animating = true
        info.firstChildPosition = firstChildPosition
        info.expanding = true
    }

    fun startCollapseAnimation(groupPosition: Int, firstChildPosition: Int = 0) {
        val info = getGroupInfo(groupPosition)
        info.animating = true
        info.firstChildPosition = firstChildPosition
        info.expanding = false
    }

    private fun stopAnimation(groupPosition: Int) {
        val info = getGroupInfo(groupPosition)
        info.animating = false
    }

    /**
     * Override [.getRealChildType] instead.
     */
    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        val info = getGroupInfo(groupPosition)
        return if (info.animating) {
            // If we are animating this group, then all of it's children
            // are going to be dummy views which we will say is type 0.
            0
        } else {
            // If we are not animating this group, then we will add 1 to
            // the type it has so that no type id conflicts will occur
            // unless getRealChildType() returns MAX_INT
            getRealChildType(groupPosition, childPosition) + 1
        }
    }

    /**
     * Override [.getRealChildTypeCount] instead.
     */
    override fun getChildTypeCount(): Int {
        // Return 1 more than the childTypeCount to account for DummyView
        return realChildTypeCount + 1
    }

    protected fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0)
    }

    /**
     * Override [.getChildView] instead.
     */
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val info = getGroupInfo(groupPosition)

        if (info.animating) {
            // If this group is animating, return the a DummyView...
            if (convertView !is DummyView) {
                convertView = DummyView(parent.context)
                convertView.setLayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, 0))
            }

            if (childPosition < info.firstChildPosition) {
                // The reason why we do this is to support the collapse
                // this group when the group view is not visible but the
                // children of this group are. When notifyDataSetChanged
                // is called, the ExpandableListView tries to keep the
                // list position the same by saving the first visible item
                // and jumping back to that item after the views have been
                // refreshed. Now the problem is, if a group has 2 navItems
                // and the first visible item is the 2nd child of the group
                // and this group is collapsed, then the dummy view will be
                // used for the group. But now the group only has 1 item
                // which is the dummy view, thus when the ListView is trying
                // to restore the scroll position, it will try to jump to
                // the second item of the group. But this group no longer
                // has a second item, so it is forced to jump to the next
                // group. This will cause a very ugly visual glitch. So
                // the way that we counteract this is by creating as many
                // dummy views as we need to maintain the scroll position
                // of the ListView after notifyDataSetChanged has been
                // called.
                convertView.getLayoutParams()?.height = 0
                return convertView
            }

            val listView = parent as ExpandableListView

            val dummyView = convertView

            // Clear the views that the dummy view draws.
            dummyView.clearViews()

            // Set the style of the divider
            dummyView.setDivider(listView.divider, parent.getMeasuredWidth(), listView.dividerHeight)

            // Make measure specs to measure child views
            val measureSpecW = MeasureSpec.makeMeasureSpec(parent.getWidth(), MeasureSpec.EXACTLY)
            val measureSpecH = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

            var totalHeight = 0
            val clipHeight = parent.getHeight()

            val len = getRealChildrenCount(groupPosition)
            for (i in info.firstChildPosition until len) {
                val childView = getRealChildView(groupPosition, i, i == len - 1, null, parent)

                var p: LayoutParams? = childView.getLayoutParams() as LayoutParams
                if (p == null) {
                    p = generateDefaultLayoutParams() as LayoutParams
                    childView.setLayoutParams(p)
                }

                val lpHeight = p!!.height

                val childHeightSpec: Int
                if (lpHeight > 0) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY)
                } else {
                    childHeightSpec = measureSpecH
                }

                childView.measure(measureSpecW, childHeightSpec)
                totalHeight += childView.getMeasuredHeight()

                if (totalHeight < clipHeight) {
                    // we only need to draw enough views to fool the user...
                    dummyView.addFakeView(childView)
                } else {
                    dummyView.addFakeView(childView)

                    // if this group has too many views, we don't want to
                    // calculate the height of everything... just do a light
                    // approximation and break
                    val averageHeight = totalHeight / (i + 1)
                    totalHeight += (len - i - 1) * averageHeight
                    break
                }
            }

            val o: Any? = dummyView.getTag()
            val state = if (o == null) STATE_IDLE else o as Int

            if (info.expanding && state != STATE_EXPANDING) {
                val ani = ExpandAnimation(dummyView, 0, totalHeight, info)
                ani.setDuration(AnimatedExpandableListView.ANIMATION_DURATION)
                ani.setAnimationListener(object : AnimationListener {

                    override fun onAnimationEnd(animation: Animation) {
                        stopAnimation(groupPosition)
                        notifyDataSetChanged()
                        dummyView.setTag(STATE_IDLE)
                    }

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationStart(animation: Animation) {}

                })
                dummyView.startAnimation(ani)
                dummyView.setTag(STATE_EXPANDING)
            } else if (!info.expanding && state != STATE_COLLAPSING) {
                if (info.dummyHeight === -1) {
                    info.dummyHeight = totalHeight
                }

                val ani = ExpandAnimation(dummyView, info.dummyHeight, 0, info)
                ani.setDuration(AnimatedExpandableListView.ANIMATION_DURATION)
                ani.setAnimationListener(object : AnimationListener {

                    override fun onAnimationEnd(animation: Animation) {
                        stopAnimation(groupPosition)
                        listView.collapseGroup(groupPosition)
                        notifyDataSetChanged()
                        info.dummyHeight = -1
                        dummyView.setTag(STATE_IDLE)
                    }

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationStart(animation: Animation) {}

                })
                dummyView.startAnimation(ani)
                dummyView.setTag(STATE_COLLAPSING)
            }

            return convertView
        } else {
            return getRealChildView(groupPosition, childPosition, isLastChild, convertView, parent)
        }
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val info = getGroupInfo(groupPosition)
        return if (info.animating) {
            info.firstChildPosition + 1
        } else {
            getRealChildrenCount(groupPosition)
        }
    }

    companion object {

        private val STATE_IDLE = 0
        private val STATE_EXPANDING = 1
        private val STATE_COLLAPSING = 2
    }

}

private class DummyView(context: Context) : View(context) {
    private val views = ArrayList<View>()
    private var divider: Drawable? = null
    private var dividerWidth: Int = 0
    private var dividerHeight: Int = 0

    fun setDivider(divider: Drawable?, dividerWidth: Int, dividerHeight: Int) {
        if (divider != null) {
            this.divider = divider
            this.dividerWidth = dividerWidth
            this.dividerHeight = dividerHeight

            divider.setBounds(0, 0, dividerWidth, dividerHeight)
        }
    }

    /**
     * Add a view for the DummyView to draw.
     * @param childView View to draw
     */
    fun addFakeView(childView: View) {
        childView.layout(0, 0, getWidth(), childView.getMeasuredHeight())
        views.add(childView)
    }

    protected override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val len = views.size
        for (i in 0 until len) {
            val v = views[i]
            v.layout(left, top, left + v.getMeasuredWidth(), top + v.getMeasuredHeight())
        }
    }

    fun clearViews() {
        views.clear()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        if (divider != null) {
            divider!!.setBounds(0, 0, dividerWidth, dividerHeight)
        }

        val len = views.size
        for (i in 0 until len) {
            val v = views[i]

            canvas.save()
            canvas.clipRect(0, 0, getWidth(), v.getMeasuredHeight())
            v.draw(canvas)
            canvas.restore()

            if (divider != null) {
                divider!!.draw(canvas)
                canvas.translate(0f, dividerHeight.toFloat())
            }

            canvas.translate(0f, v.getMeasuredHeight().toFloat())
        }

        canvas.restore()
    }
}


class ExpandAnimation constructor(private val view: View, private val baseHeight: Int, endHeight: Int, private val groupInfo: GroupInfo) : Animation() {
    private val delta: Int = endHeight - baseHeight

    init {

        view.getLayoutParams().height = baseHeight
        view.requestLayout()
    }

    protected override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            val `val` = baseHeight + (delta * interpolatedTime).toInt()
            view.getLayoutParams().height = `val`
            groupInfo.dummyHeight = `val`
            view.requestLayout()
        } else {
            val `val` = baseHeight + delta
            view.getLayoutParams().height = `val`
            groupInfo.dummyHeight = `val`
            view.requestLayout()
        }
    }
}

/**
 * Used for holding information regarding the group.
 */
class GroupInfo {
    internal var animating = false
    internal var expanding = false
    internal var firstChildPosition: Int = 0

    /**
     * This variable contains the last known height value of the dummy view.
     * We save this information so that if the user collapses a group
     * before it fully expands, the collapse animation will start from the
     * CURRENT height of the dummy view and not from the full expanded
     * height.
     */
    internal var dummyHeight = -1
}