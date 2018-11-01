package com.example.linh.vietkitchen.extension

import android.text.Annotation
import android.text.Spannable
import android.text.SpannableStringBuilder
import timber.log.Timber
import java.util.regex.Pattern

fun CharSequence.attractAnnotationSpan(): Array<out Annotation>? {
    if(isEmpty()) return arrayOf()
    val spannable = this as Spannable
    return spannable.getSpans(0, spannable.length, Annotation::class.java)
}

fun CharSequence.attractUrlFromAnnotation(): List<String>? {
    if(isEmpty()) return null
    val srcPattern = Pattern.compile("(?<=<annotation src=\")(.*?)(?=\"/>)")
    val matcher = srcPattern.matcher(this)
    val result = mutableListOf<String>()
    while (matcher.find()){
        result.add(this.substring(matcher.start(), matcher.end()))
    }
    return result
}

fun CharSequence.generateAnnotationSpan(): CharSequence{
    val source = this
    val ssb = SpannableStringBuilder(source)
    val imgAnnotationPattern: Pattern = Pattern.compile("(<annotation src=\"(.*?)\"/>)")
    val srcPattern = Pattern.compile("(?<=src=\")(.*?)(?=\")")
    val matcher = imgAnnotationPattern.matcher(source)
    var lastEnd = 0
    while (matcher.find()){
        val startAnnotation = matcher.start()
        val endAnnotation = matcher.end()
        Timber.d(startAnnotation.toString() + " - " + endAnnotation.toString())

        //add paragraph annotation
        ssb.setSpan(Annotation("p", "start"), lastEnd, startAnnotation, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        lastEnd = endAnnotation

        //add image annotation
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