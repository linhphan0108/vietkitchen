package com.example.linh.vietkitchen.ui.model

import android.net.Uri

data class UserInfo(val uid: String, val displayName: String?, val email: String?,
                    val avatarUrl: Uri?, var numberFavoriteRecipes: Int = 0, var numberScheduleRecipes: Int = 0,
                    var likedRecipesIds: MutableList<String>? = null, val scheduledRecipeId: List<String>? = null,
                    var allowNotification: Boolean = true)