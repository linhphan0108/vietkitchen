package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.getDimension

import java.util.ArrayList
import java.util.Collections

import com.example.linh.vietkitchen.extension.getDimensionPixelSize


/**
 * Created by Mohammad Abbas on 5/10/16.
 * Needs a lot of work
 * BETA
 * https://github.com/mabbas007/TagsEditText
 */
class TagsEditText : AppCompatAutoCompleteTextView {

    private var mSeparator = " "
    private var mLastString: String? = ""
    private var mIsAfterTextWatcherEnabled = true

    private var mTagsTextColor: Int = 0
    private var mTagsTextSize: Float = 0.toFloat()
    private var mTagsBackground: Drawable? = null
    private var mTagsBackgroundResource = 0
    private var mLeftDrawable: Drawable? = null
    private var mLeftDrawableResouce = 0

    private var mRightDrawable: Drawable? = null
    private var mRightDrawableResouce = 0

    private var mDrawablePadding: Int = 0

    private var mTagsPaddingLeft: Int = 0
    private var mTagsPaddingRight: Int = 0
    private var mTagsPaddingTop: Int = 0
    private var mTagsPaddingBottom: Int = 0

    private var mIsSpacesAllowedInTags = false
    private var mIsSetTextDisabled = false

    private var shouldDisableKeyboard = false

    private val mTagSpans = ArrayList<TagSpan>()
    private var mTags: MutableList<Tag> = ArrayList()

    private var mListener: TagsEditListener? = null

    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (mIsAfterTextWatcherEnabled) {
                setTags()
            }
        }
    }

    val tags: List<String>
        get() = convertTagSpanToList(mTagSpans)

    fun setSeparator(separator: String) {
        mSeparator = separator
    }

    constructor(context: Context) : super(context) {
        init(null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr, 0)
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
//        init(attrs, defStyleAttr, defStyleRes)
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (shouldDisableKeyboard) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)  // hide the soft keyboard
            val inType = inputType // backup the input type
            inputType = InputType.TYPE_NULL // disable soft input
            super.onTouchEvent(event) // call native handler
            inputType = inType // restore input type
            true // consume touch even
        }else{
            super.onTouchEvent(event)
        }

    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (text != null) {
            setSelection(text.length)
        } else {
            super.onSelectionChanged(selStart, selEnd)
        }
    }

    /**
     * do not use this method to set tags
     */
    override fun setText(text: CharSequence, type: TextView.BufferType) {
        if (mIsSetTextDisabled) return
        if (!TextUtils.isEmpty(text)) {
            val source = if (mIsSpacesAllowedInTags) text.toString().trim { it <= ' ' } else text.toString().replace(" ".toRegex(), "")
            if (mTags.isEmpty()) {
                val tag = Tag()
                tag.index = 0
                tag.position = 0
                tag.source = source
                tag.isSpan = true
                mTags.add(tag)
            } else {
                val size = mTags.size
                val lastTag = mTags[size - 1]
                if (!lastTag.isSpan) {
                    lastTag.source = source
                    lastTag.isSpan = true
                } else {
                    val newTag = Tag()
                    newTag.index = size
                    newTag.position = lastTag.position + lastTag.source!!.length + 1
                    newTag.source = source
                    newTag.isSpan = true
                    mTags.add(newTag)
                }
            }
            buildStringWithTags(mTags)
            mTextWatcher.afterTextChanged(getText())
        } else {
            super.setText(text, type)
        }
    }

    /**
     * use this method to set tags
     */
    fun setTags(vararg tags: CharSequence) {
        mTagSpans.clear()
        mTags.clear()

        val length = tags?.size ?: 0
        var position = 0
        for (i in 0 until length) {
            val tag = Tag()
            tag.index = i
            tag.position = position
            val source = if (mIsSpacesAllowedInTags) tags[i].toString().trim { it <= ' ' } else tags[i].toString().replace(" ".toRegex(), "")
            tag.source = source
            tag.isSpan = true
            mTags.add(tag)
            position += source.length + 1
        }
        buildStringWithTags(mTags)
        mTextWatcher.afterTextChanged(text)
    }

    /**
     * use this method to set tags
     */

    fun setTags(tags: Array<String>?) {
        mTagSpans.clear()
        mTags.clear()

        val length = tags?.size ?: 0
        var position = 0
        for (i in 0 until length) {
            val tag = Tag()
            tag.index = i
            tag.position = position
            val source = if (mIsSpacesAllowedInTags) tags!![i].trim { it <= ' ' } else tags!![i].replace(" ".toRegex(), "")
            tag.source = source
            tag.isSpan = true
            mTags.add(tag)
            position += source.length + 1
        }
        buildStringWithTags(mTags)
        mTextWatcher.afterTextChanged(text)
    }

    override fun onSaveInstanceState(): Parcelable? {

        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())

        val tags = arrayOfNulls<Tag>(mTags.size)
        mTags.toTypedArray()

        bundle.putParcelableArray(TAGS, tags)
        bundle.putString(LAST_STRING, mLastString)
        bundle.putString(UNDER_CONSTRUCTION_TAG, getNewTag(text.toString()))

        bundle.putInt(TAGS_TEXT_COLOR, mTagsTextColor)
        bundle.putInt(TAGS_BACKGROUND_RESOURCE, mTagsBackgroundResource)
        bundle.putFloat(TAGS_TEXT_SIZE, mTagsTextSize)
        bundle.putInt(LEFT_DRAWABLE_RESOURCE, mLeftDrawableResouce)
        bundle.putInt(RIGHT_DRAWABLE_RESOURCE, mRightDrawableResouce)
        bundle.putInt(DRAWABLE_PADDING, mDrawablePadding)
        bundle.putBoolean(ALLOW_SPACES_IN_TAGS, mIsSpacesAllowedInTags)

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state

        if (state is Bundle) {
            val context = context
            val bundle = state as Bundle?

            mTagsTextColor = bundle!!.getInt(TAGS_TEXT_COLOR, mTagsTextColor)

            mTagsBackgroundResource = bundle.getInt(TAGS_BACKGROUND_RESOURCE, mTagsBackgroundResource)
            if (mTagsBackgroundResource != 0) {
                mTagsBackground = ContextCompat.getDrawable(context, mTagsBackgroundResource)
            }

            mTagsTextSize = bundle.getFloat(TAGS_TEXT_SIZE, mTagsTextSize)

            mLeftDrawableResouce = bundle.getInt(LEFT_DRAWABLE_RESOURCE, mLeftDrawableResouce)
            if (mLeftDrawableResouce != 0) {
                mLeftDrawable = ContextCompat.getDrawable(context, mLeftDrawableResouce)
            }

            mRightDrawableResouce = bundle.getInt(RIGHT_DRAWABLE_RESOURCE, mRightDrawableResouce)
            if (mRightDrawableResouce != 0) {
                mRightDrawable = ContextCompat.getDrawable(context, mRightDrawableResouce)
            }

            mDrawablePadding = bundle.getInt(DRAWABLE_PADDING, mDrawablePadding)
            mIsSpacesAllowedInTags = bundle.getBoolean(ALLOW_SPACES_IN_TAGS, mIsSpacesAllowedInTags)

            mLastString = bundle.getString(LAST_STRING)
            val tagsParcelables = bundle.getParcelableArray(TAGS)
            if (tagsParcelables != null) {
                val tags = arrayOfNulls<Tag>(tagsParcelables.size)
                System.arraycopy(tagsParcelables, 0, tags, 0, tagsParcelables.size)
                mTags = ArrayList()
                Collections.addAll<Tag>(mTags, *tags)
                buildStringWithTags(mTags)
                mTextWatcher.afterTextChanged(text)
            }
            state = bundle.getParcelable(SUPER_STATE)
            mIsSetTextDisabled = true
            super.onRestoreInstanceState(state)
            mIsSetTextDisabled = false

            val temp = bundle.getString(UNDER_CONSTRUCTION_TAG)
            if (!TextUtils.isEmpty(temp))
                text.append(temp)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun buildStringWithTags(tags: List<Tag>) {
        mIsAfterTextWatcherEnabled = false
        text.clear()
        for (tag in tags) {
            text.append(tag.source).append(mSeparator)
        }
        mLastString = text.toString()
        if (!TextUtils.isEmpty(mLastString)) {
            text.append(NEW_LINE)
        }
        mIsAfterTextWatcherEnabled = true
    }

    fun setTagsTextColor(@ColorRes color: Int) {
        mTagsTextColor = getColor(context, color)
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setTagsTextSize(@DimenRes textSize: Int) {
        mTagsTextSize = getDimension(textSize)
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setTagsBackground(@DrawableRes drawable: Int) {
        mTagsBackground = ContextCompat.getDrawable(context, drawable)
        mTagsBackgroundResource = drawable
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setCloseDrawableLeft(@DrawableRes drawable: Int) {
        mLeftDrawable = ContextCompat.getDrawable(context, drawable)
        mLeftDrawableResouce = drawable
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setCloseDrawableRight(@DrawableRes drawable: Int) {
        mRightDrawable = ContextCompat.getDrawable(context, drawable)
        mRightDrawableResouce = drawable
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setCloseDrawablePadding(@DimenRes padding: Int) {
        mDrawablePadding = getDimensionPixelSize(padding)
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setTagsWithSpacesEnabled(isSpacesAllowedInTags: Boolean) {
        mIsSpacesAllowedInTags = isSpacesAllowedInTags
        setTags(*convertTagSpanToArray(mTagSpans))
    }

    fun setTagsListener(listener: TagsEditListener) {
        mListener = listener
    }

    @ColorInt
    private fun getColor(context: Context, @ColorRes colorId: Int): Int {
        return if (Build.VERSION.SDK_INT >= 23) {
            ContextCompat.getColor(context, colorId)
        } else {
            context.resources.getColor(colorId)
        }
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val context = context
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagsEditText, defStyleAttr, defStyleRes)
            try {
                mIsSpacesAllowedInTags = typedArray.getBoolean(R.styleable.TagsEditText_allowSpaceInTag, false)
                mTagsTextColor = typedArray.getColor(R.styleable.TagsEditText_tagsTextColor,
                        getColor(context, R.color.defaultTagsTextColor))
                mTagsTextSize = typedArray.getDimensionPixelSize(R.styleable.TagsEditText_tagsTextSize,
                        getDimensionPixelSize(R.dimen.defaultTagsTextSize)).toFloat()
                mTagsBackground = typedArray.getDrawable(R.styleable.TagsEditText_tagsBackground)
                mRightDrawable = typedArray.getDrawable(R.styleable.TagsEditText_tagsCloseImageRight)
                mLeftDrawable = typedArray.getDrawable(R.styleable.TagsEditText_tagsCloseImageLeft)
                mDrawablePadding = typedArray.getDimensionPixelOffset(R.styleable.TagsEditText_tagsCloseImagePadding,
                        getDimensionPixelSize(R.dimen.defaultTagsCloseImagePadding))
                mTagsPaddingRight = typedArray.getDimensionPixelSize(R.styleable.TagsEditText_tagsPaddingRight,
                        getDimensionPixelSize(R.dimen.defaultTagsPadding))
                mTagsPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.TagsEditText_tagsPaddingLeft,
                        getDimensionPixelSize(R.dimen.defaultTagsPadding))
                mTagsPaddingTop = typedArray.getDimensionPixelSize(R.styleable.TagsEditText_tagsPaddingTop,
                        getDimensionPixelSize(R.dimen.defaultTagsPadding))
                mTagsPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.TagsEditText_tagsPaddingBottom,
                        getDimensionPixelSize(R.dimen.defaultTagsPadding))
                shouldDisableKeyboard = typedArray.getBoolean(R.styleable.TagsEditText_shouldDisableKeyboard, false)
            } finally {
                typedArray.recycle()
            }
        }

        initDefaultIfNeed()

        movementMethod = LinkMovementMethod.getInstance()
        inputType = (InputType.TYPE_CLASS_TEXT
                or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        val viewTreeObserver = viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    }
                    addTextChangedListener(mTextWatcher)
                    mTextWatcher.afterTextChanged(text)
                }
            })
        }
    }

    private fun initDefaultIfNeed(){
        //        mIsSpacesAllowedInTags = false
        if (mTagsTextColor == 0 ) {
            mTagsTextColor = getColor(context, R.color.defaultTagsTextColor)
        }
        if(mTagsTextSize == 0f) {
            mTagsTextSize = getDimensionPixelSize(R.dimen.defaultTagsTextSize).toFloat()
        }
        if(mTagsBackground == null) {
            mTagsBackground = ContextCompat.getDrawable(context, R.drawable.bg_tag_default)
        }
        if (mRightDrawable == null) {
            mRightDrawable = ContextCompat.getDrawable(context, R.drawable.tag_close)
        }
        if (mDrawablePadding == 0) {
            mDrawablePadding = getDimensionPixelSize(R.dimen.defaultTagsCloseImagePadding)
        }
        if (mTagsPaddingRight == 0) {
            mTagsPaddingRight = getDimensionPixelSize(R.dimen.defaultTagsPadding)
        }
        if (mTagsPaddingLeft == 0) {
            mTagsPaddingLeft = getDimensionPixelSize(R.dimen.defaultTagsPadding)
        }
        if (mTagsPaddingTop == 0) {
            mTagsPaddingTop = getDimensionPixelSize(R.dimen.defaultTagsPadding)
        }
        if (mTagsPaddingBottom == 0) {
            mTagsPaddingBottom = getDimensionPixelSize(R.dimen.defaultTagsPadding)
        }
    }

    private fun setTags() {
        mIsAfterTextWatcherEnabled = false
        var isEnterClicked = false

        val editable = text
        var str = editable.toString()
        if (str.endsWith(NEW_LINE)) {
            isEnterClicked = true
        }

        val isDeleting = mLastString!!.length > str.length
        if (mLastString!!.endsWith(mSeparator)
                && !str.endsWith(NEW_LINE)
                && isDeleting
                && !mTagSpans.isEmpty()) {
            val toRemoveSpan = mTagSpans[mTagSpans.size - 1]
            val tag = toRemoveSpan.tag
            if (tag!!.position + tag.source!!.length == str.length) {
                removeTagSpan(editable, toRemoveSpan, false)
                str = editable.toString()
            }
        }

        if (filter != null) {
            performFiltering(getNewTag(str), 0)
        }

        if (str.endsWith(NEW_LINE) || !mIsSpacesAllowedInTags && str.endsWith(mSeparator) && !isDeleting) {
            buildTags(str)
        }

        mLastString = text.toString()
        mIsAfterTextWatcherEnabled = true
        if (isEnterClicked && mListener != null) {
            mListener!!.onEditingFinished()
        }
    }

    private fun buildTags(str: String) {
        if (str.isNotEmpty()) {
            updateTags(str)

            val sb = SpannableStringBuilder()
            for (tagSpan in mTagSpans) {
                addTagSpan(sb, tagSpan)
            }

            val size = mTags.size
            for (i in mTagSpans.size until size) {
                val tag = mTags[i]
                val source = tag.source
                if (tag.isSpan) {
                    val tv = createTextView(source)
                    val bd = convertViewToDrawable(tv)
                    bd.setBounds(0, 0, bd.intrinsicWidth, bd.intrinsicHeight)
                    val span = TagSpan(bd, source!!)
                    addTagSpan(sb, span)
                    span.tag = tag
                    mTagSpans.add(span)
                } else {
                    sb.append(source)
                }
            }

            text.clear()
            text.append(sb)
            movementMethod = LinkMovementMethod.getInstance()
            setSelection(sb.length)
            if (mListener != null && str != mLastString) {
                mListener!!.onTagsChanged(convertTagSpanToList(mTagSpans))
            }
        }
    }

    private fun updateTags(newString: String) {
        var source = getNewTag(newString)
        if (!TextUtils.isEmpty(source) && source != NEW_LINE) {
            val isSpan = source.endsWith(NEW_LINE) || !mIsSpacesAllowedInTags && source.endsWith(mSeparator)
            if (isSpan) {
                source = source.substring(0, source.length - 1)
                source = source.trim { it <= ' ' }
            }
            val tag = Tag()
            tag.source = source
            tag.isSpan = isSpan
            val size = mTags.size
            if (size <= 0) {
                tag.index = 0
                tag.position = 0
            } else {
                val lastTag = mTags[size - 1]
                tag.index = size
                tag.position = lastTag.position + lastTag.source!!.length + 1
            }
            mTags.add(tag)
        }
    }

    private fun getNewTag(newString: String): String {
        val builder = StringBuilder()
        for (tag in mTags) {
            if (!tag.isSpan) continue
            builder.append(tag.source).append(mSeparator)
        }
        return newString.replace(builder.toString(), "")
    }

    private fun addTagSpan(sb: SpannableStringBuilder, tagSpan: TagSpan) {
        val source = tagSpan.source
        sb.append(source).append(mSeparator)
        val length = sb.length
        val startSpan = length - (source.length + 1)
        val endSpan = length - 1
        sb.setSpan(tagSpan, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val editable = (widget as EditText).text
                mIsAfterTextWatcherEnabled = false
                removeTagSpan(editable, tagSpan, true)
                mIsAfterTextWatcherEnabled = true
            }
        }, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun removeTagSpan(editable: Editable, span: TagSpan, includeSpace: Boolean) {
        val extraLength = if (includeSpace) 1 else 0
        // include space
        val tag = span.tag
        val tagPosition = tag!!.position
        val tagIndex = tag.index
        val tagLength = span.source.length + extraLength
        editable.replace(tagPosition, tagPosition + tagLength, "")
        val size = mTags.size
        for (i in tagIndex + 1 until size) {
            val newTag = mTags[i]
            newTag.index = i - 1
            newTag.position = newTag.position - tagLength
        }
        mTags.removeAt(tagIndex)
        mTagSpans.removeAt(tagIndex)
        if (mListener == null) return
        mListener!!.onTagsChanged(convertTagSpanToList(mTagSpans))
    }

    private fun convertViewToDrawable(view: View): Drawable {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val b = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate((-view.scrollX).toFloat(), (-view.scrollY).toFloat())
        view.draw(c)
        view.isDrawingCacheEnabled = true
        val cacheBmp = view.drawingCache
        val viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true)
        view.destroyDrawingCache()
        return BitmapDrawable(resources, viewBmp)
    }

    private fun createTextView(text: String?): TextView {
        val textView = TextView(context)
        if (width > 0) {
            textView.maxWidth = width - 50
        }
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTagsTextSize)
        textView.setTextColor(mTagsTextColor)
        textView.setPadding(mTagsPaddingLeft, mTagsPaddingTop, mTagsPaddingRight, mTagsPaddingBottom)

        // check Android version for set background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.background = mTagsBackground
        } else {
            textView.setBackgroundDrawable(mTagsBackground)
        }

        textView.setCompoundDrawablesWithIntrinsicBounds(mLeftDrawable, null, mRightDrawable, null)
        textView.compoundDrawablePadding = mDrawablePadding
        return textView
    }


    private class Tag() : Parcelable {
        override fun writeToParcel(dest: Parcel?, flags: Int) {
            TODO("not implemented") //To change data of created functions use File | Settings | File Templates.
        }

        override fun describeContents(): Int {
            TODO("not implemented") //To change data of created functions use File | Settings | File Templates.
        }

        internal var position: Int = 0
        internal var index: Int = 0
        var source: String? = null
        var isSpan: Boolean = false

        constructor(parcel: Parcel) : this() {
            position = parcel.readInt()
            index = parcel.readInt()
            source = parcel.readString()
            isSpan = parcel.readByte() != 0.toByte()
        }

        companion object CREATOR : Parcelable.Creator<Tag> {
            override fun createFromParcel(parcel: Parcel): Tag {
                return Tag(parcel)
            }

            override fun newArray(size: Int): Array<Tag?> {
                return arrayOfNulls(size)
            }
        }
    }

    private class TagSpan : ImageSpan {

        var tag: Tag? = null

        constructor(d: Drawable, source: String) : super(d, source) {}

        // private constructors

        private constructor(context: Context, b: Bitmap) : super(context, b) {}

        private constructor(context: Context, b: Bitmap, verticalAlignment: Int) : super(context, b, verticalAlignment) {}

        private constructor(d: Drawable) : super(d) {}

        private constructor(d: Drawable, verticalAlignment: Int) : super(d, verticalAlignment) {}

        private constructor(d: Drawable, source: String, verticalAlignment: Int) : super(d, source, verticalAlignment) {}

        private constructor(context: Context, uri: Uri) : super(context, uri) {}

        private constructor(context: Context, uri: Uri, verticalAlignment: Int) : super(context, uri, verticalAlignment) {}

        private constructor(context: Context, resourceId: Int) : super(context, resourceId) {}

        private constructor(context: Context, resourceId: Int, verticalAlignment: Int) : super(context, resourceId, verticalAlignment) {}

    }

    interface TagsEditListener {

        fun onTagsChanged(tags: Collection<String>)

        fun onEditingFinished()

    }

    class TagsEditListenerAdapter : TagsEditListener {

        override fun onTagsChanged(tags: Collection<String>) {}

        override fun onEditingFinished() {}

    }

    companion object {

        val NEW_LINE = "\n"

        private val LAST_STRING = "lastString"
        private val TAGS = "tags"
        private val SUPER_STATE = "superState"
        private val UNDER_CONSTRUCTION_TAG = "underConstructionTag"
        private val ALLOW_SPACES_IN_TAGS = "allowSpacesInTags"

        private val TAGS_BACKGROUND_RESOURCE = "tagsBackground"
        private val TAGS_TEXT_COLOR = "tagsTextColor"
        private val TAGS_TEXT_SIZE = "tagsTextSize"
        private val LEFT_DRAWABLE_RESOURCE = "leftDrawable"
        private val RIGHT_DRAWABLE_RESOURCE = "rightDrawable"
        private val DRAWABLE_PADDING = "drawablePadding"

        private fun convertTagSpanToList(tagSpans: List<TagSpan>): List<String> {
            val tags = ArrayList<String>(tagSpans.size)
            for (tagSpan in tagSpans) {
                tags.add(tagSpan.source)
            }
            return tags
        }

        private fun convertTagSpanToArray(tagSpans: List<TagSpan>): Array<CharSequence> {
            val size = tagSpans.size
            val values = Array<CharSequence>(size) {""}
            for (i in 0 until size) {
                values[i] = tagSpans[i].source
            }
            return values
        }
    }

}
