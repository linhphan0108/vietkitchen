package com.example.linh.vietkitchen.extension

import android.text.Annotation
import android.text.Spannable
import android.text.SpannableStringBuilder
import timber.log.Timber
import java.util.regex.Pattern

/**
 * Returns `true` if this nullable char sequence is either `null` or empty or consists solely of whitespace characters.
 */
fun CharSequence?.isNotNullAndNotBlank(): Boolean {
    return this != null && this.isNotBlank()
}

fun CharSequence.attractAnnotationSpan(): Array<out Annotation>? {
    if(isBlank()) return arrayOf()
    val spannable = this as Spannable
    return spannable.getSpans(0, spannable.length, Annotation::class.java)
}

fun CharSequence.attractUrlFromAnnotation(): List<String>? {
    if(isBlank()) return null
    val srcPattern = Pattern.compile("(?<=<annotation src=\")(.*?)(?=\"/>)")
    val matcher = srcPattern.matcher(this)
    val result = mutableListOf<String>()
    while (matcher.find()){
        result.add(this.substring(matcher.start(), matcher.end()))
    }
    return result
}

fun CharSequence.generateAnnotationSpan(): CharSequence{
    if (isBlank()) return ""
    val source = this
    val ssb = SpannableStringBuilder(source)
    val imgAnnotationPattern: Pattern = Pattern.compile("(<annotation src=\"(.*?)\"/>)")
    val srcPattern = Pattern.compile("(?<=src=\")(.*?)(?=\")")
    val matcher = imgAnnotationPattern.matcher(source)
    while (matcher.find()){
        val startAnnotation = matcher.start()
        val endAnnotation = matcher.end()
        Timber.d("$startAnnotation - $endAnnotation")

        //add image'url into the annotation
        val imgAnnotation = source.substring(startAnnotation, endAnnotation)
        val srcMatcher = srcPattern.matcher(imgAnnotation)
        if (srcMatcher.find()){
            System.out.println("src = " + srcMatcher.start().toString() + " - " + srcMatcher.end().toString())
            val url = imgAnnotation.substring(srcMatcher.start(), srcMatcher.end())
            ssb.setSpan(Annotation("src", url), startAnnotation, endAnnotation, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return ssb
}