package com.example.linh.vietkitchen.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.linh.vietkitchen.R

class MainTabBar : LinearLayout, View.OnClickListener {

    private var  attrs: AttributeSet? = null
    private val iBtnHomeTab: ImageButton by lazy { findViewById<ImageButton>(R.id.iBtnHome) }
    private val iBtnFavoriteTab: ImageButton by lazy { findViewById<ImageButton>(R.id.iBtnFavorite) }
    private val iBtnCalendarTab: ImageButton by lazy { findViewById<ImageButton>(R.id.iBtnCalendar) }
    private val iBtnProfileTab: ImageButton by lazy { findViewById<ImageButton>(R.id.iBtnProfile) }

    var currentTab: Tab = Tab.TAB_HOME
    var listener: OnTabChange? = null

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : super(context, attrs, defStyleAttr){
        this.attrs = attrs
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes){
        this.attrs = attrs
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.main_tabbar_layout, this, true)
        orientation = HORIZONTAL
        attrs?.let {
//            val typedArray = context.obtainStyledAttributes(it,
//                    R.styleable., 0, 0)
//            typedArray.recycle()
        }
        setEventListeners()
    }

    //region callbacks==============================================================================
    override fun onClick(v: View?) {
        when (v?.id){
            R.id.iBtnHome -> onHomeTabSelected()
            R.id.iBtnFavorite -> onFavoriteTabSelected()
            R.id.iBtnCalendar -> onCalendarTabSelected()
            R.id.iBtnProfile -> onProfileTabSelected()
            else -> Unit
        }
    }

    //endregion

    //region inner methods =========================================================================
    private fun setEventListeners(){
        iBtnHomeTab.setOnClickListener(this)
        iBtnFavoriteTab.setOnClickListener(this)
        iBtnCalendarTab.setOnClickListener(this)
        iBtnProfileTab.setOnClickListener(this)
    }

    private fun onHomeTabSelected(){
        if (setTabSelected(Tab.TAB_HOME)){
            listener?.onTabHomeSelected()
        }

    }

    private fun onFavoriteTabSelected(){
        if (setTabSelected(Tab.TAB_FAVORITE)){
            listener?.onTabFavoriteSelected()
        }
    }

    private fun onCalendarTabSelected(){
        if (setTabSelected(Tab.TAB_CALENDAR)){
            listener?.onTabCalendarSelected()
        }
    }

    private fun onProfileTabSelected(){
        if (setTabSelected(Tab.TAB_PROFILE)){
            listener?.onTabProfileSelected()
        }
    }

    private fun getViewByTab(tab: Tab): ImageButton{
        return when(tab){
            Tab.TAB_HOME -> iBtnHomeTab
            Tab.TAB_FAVORITE -> iBtnFavoriteTab
            Tab.TAB_CALENDAR -> iBtnCalendarTab
            Tab.TAB_PROFILE -> iBtnProfileTab
        }
    }

    private fun setTabSelected(tab: Tab) : Boolean{
        if (currentTab != tab){
            getViewByTab(currentTab).isSelected = false
            getViewByTab(tab).isSelected = true
            currentTab = tab
            return true
        }
        return false
    }

    fun getCurrentTabSelected() = currentTab

    fun setTabListener(listener: OnTabChange){
        this.listener = listener
    }
    //endregion


    //region inner classes =========================================================================
    enum class Tab{
        TAB_HOME, TAB_FAVORITE, TAB_CALENDAR, TAB_PROFILE
    }

    interface OnTabChange{
        fun onTabHomeSelected()
        fun onTabFavoriteSelected()
        fun onTabCalendarSelected()
        fun onTabProfileSelected()
    }
    //endregion
}