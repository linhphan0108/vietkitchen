package com.example.linh.vietkitchen.util

object Constants {
    //firebase realtime data path
    const val STORAGE_RECIPES_PATH = "recipes"
    const val STORAGE_RECIPES_TAGS_PATH = "tag"
    const val STORAGE_RECIPES_CHILD_TAGS = "tags"
    const val STORAGE_RECIPES_CHILD_CATEGORIES = "categories"
    const val STORAGE_USER_LIKED_RECIPES_PATH = "likedRecipes"
    const val STORAGE_USER_PATH = "users"

    const val BK_CATEGORIES = "CATEGORIES"

    const val PAGINATION_LENGTH = 10
    const val VISIBLE_THRESHOLD_TO_LOAD_MORE = 3
}

enum class RecyclerViewLayoutMode{
    MODE_LINENEAR_VERTICAL,
    MODE_STAGGERED_VERTICAL
}