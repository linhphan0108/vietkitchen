package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

enum class AnnotationKey(val key: String, val value: String? = null) {
    STYLE("style"), IMAGE("src"), PARAGRAPH_SPACE("para-space", "")
}

enum class Style{
    NORMAL {
        override fun toString(): String {
            return "normal"
        }
    }

    , BOLD {
        override fun toString(): String {
            return "bold"
        }
    }
    , ITALIC {
        override fun toString(): String {
            return "italic"
        }
    }, BOLD_ITALIC;

    override fun toString(): String {
        return "bold_italic"
    }
}