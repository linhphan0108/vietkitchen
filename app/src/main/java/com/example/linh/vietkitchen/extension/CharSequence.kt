package com.example.linh.vietkitchen.extension

import android.text.Annotation
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.example.linh.vietkitchen.ui.custom.imageSpanWidget.AnnotationKey
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
    val sb = StringBuffer()
    val ssb = SpannableStringBuilder()
    var body: String
    var key: String
    var value: String

    val annotationPattern: Pattern = Pattern.compile("(<annotation\\s*\\w+=\".+?\"\\s*((/>)|(>.*?</annotation>)))|(\\n+)")
    val annotationMatcher = annotationPattern.matcher(source)
    val annotationTypePattern = Pattern.compile("(?<=<annotation\\s)(\\w+)(?==\")")
//    Timber.d("**********************************************")
    while (annotationMatcher.find()){
        //clear the string buffer
        sb.setLength(0)
        key = ""
        value = ""
        body = ""
        val annotationText = annotationMatcher.group()// get the match

        if (annotationText.isBlank()){
            //add annotation onto space between paragraphs
            body = "\n\n"
            annotationMatcher.appendReplacement(sb, body)
            ssb.append(sb)
            ssb.setSpan(Annotation(AnnotationKey.PARAGRAPH_SPACE.key, AnnotationKey.PARAGRAPH_SPACE.value)
                    , ssb.length - body.length +1, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }else {
            val annotationKeyMatcher = annotationTypePattern.matcher(annotationText)
//        Timber.d("annotationText ${++counter} = $annotationText")
            if (annotationKeyMatcher.find(0)) {
                key = annotationKeyMatcher.group().trim()
//            Timber.d("annotation type = $key")
                val annotationValuePattern = Pattern.compile("(?<=$key=\")(.*?)(?=\")")
                val annotationValueMatcher = annotationValuePattern.matcher(annotationText)
                if (annotationValueMatcher.find(0)) {
                    value = annotationValueMatcher.group()
//                Timber.d("annotation value = $value")
                    body = when (key) {
                        AnnotationKey.STYLE.key -> {
                            val annotationBodyPattern = Pattern.compile("(?<=<annotation $key=\"$value\">).*?(?=</annotation>)")
                            val annotationBodyMatcher = annotationBodyPattern.matcher(annotationText)
                            if (annotationBodyMatcher.find(0)) {
                                annotationBodyMatcher.group()
                            } else {
                                ""
                            }
                        }
                        AnnotationKey.IMAGE.key -> {
                            "image"
                        }
                        else -> {
                            ""
                        }
                    }
                }
            }
            // appendReplacement handles replacing within the current match's bounds
            annotationMatcher.appendReplacement(sb, body)
            ssb.append(sb)
            ssb.setSpan(Annotation(key, value), ssb.length - body.length, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        Timber.d("key = $key - value = $value")
        }
    }
//    Timber.d("**********************************************")

    // clear the string buffer
    sb.setLength(0)
    //add any text left over after the final match
    annotationMatcher.appendTail(sb)
    ssb.append(sb)

    return ssb
}

fun CharSequence.findLastIndex(predicate: (char: Char) -> Boolean): Int{
    if (isNullOrEmpty()) return -1
    for (i in length downTo 0 step 1){
        if (predicate(this[i])) return i
    }
    return -1
}

fun CharSequence.isLastLineBlank(): Boolean{
    if (isNotNullAndNotBlank()) {
        val lastBreakLine = findLastIndex { it == '\n' }
        if (lastBreakLine > -1) {
            val lastLine = this.subSequence(lastBreakLine, length)
            return lastLine.isBlank()
        }
    }
    return true
}

fun CharSequence.breakLineFirst(): CharSequence {
    return "${System.getProperty("line.separator")}$this"
}

fun CharSequence.breakLineLast(): CharSequence {
    return "$this${System.getProperty("line.separator")}"
}

fun CharSequence.capParagraph(): CharSequence{
    val capitalized = this.toString().capitalize()
    val pattern = Pattern.compile("\\n+\\w")
    val matcher = pattern.matcher(capitalized)
    val ssb = SpannableStringBuilder()
    val sb = StringBuffer()
    while (matcher.find()){
        sb.setLength(0)
        val text = matcher.group()
        val upperCase = text.substring(text.length -2, text.length -1).toUpperCase()
        text.replaceRange(text.length -2, text.length -1, upperCase)
        matcher.appendReplacement(sb, text)
        ssb.append(sb)
    }
// clear the string buffer
    sb.setLength(0)
    //add any text left over after the final match
    matcher.appendTail(sb)
    ssb.append(sb)

    return ssb
}