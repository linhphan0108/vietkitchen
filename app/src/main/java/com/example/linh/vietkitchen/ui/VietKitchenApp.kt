package com.example.linh.vietkitchen.ui

import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDexApplication
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.util.NotLoggingTree
import com.google.firebase.auth.FirebaseAuth
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber



class VietKitchenApp : MultiDexApplication(){
    companion object {
        private  val userInfo: MutableLiveData<UserInfo> = MutableLiveData()
        val category: MutableLiveData<List<DrawerNavGroupItem>> = MutableLiveData()

        fun getUserInfo() = userInfo.value!!
        fun getUserInfoObservable() = userInfo
        fun removeLikedRecipeId(id: String){
            val removed = getUserInfo().likedRecipesIds?.remove(id) ?: false
            if(removed) {
                getUserInfo().numberFavoriteRecipes = getUserInfo().likedRecipesIds?.size ?: 0
                userInfo.postValue(getUserInfo())
            }
        }
        fun addLikedRecipeId(id: String) {
            val added = getUserInfo().likedRecipesIds?.add(id) ?: false
            if (added) {
                getUserInfo().numberFavoriteRecipes = getUserInfo().likedRecipesIds?.size ?: 0
                userInfo.postValue(getUserInfo())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initUserInfo()
        if(setupLeaksCanary()) return
        setupTimberLogger()
    }

    private fun initUserInfo(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            userInfo.value = UserInfo(currentUser.uid, currentUser.displayName, currentUser.email, currentUser.photoUrl)
        }else{
//            throw NullPointerException("user info not found!")
        }
    }

    private fun setupTimberLogger(){
        if (BuildConfig.DEBUG){
            Timber.plant(object: Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format("C:%s:%s",
                            super.createStackElementTag(element),
                            element.lineNumber)
                }
            })
        }else{
            Timber.plant(NotLoggingTree())
        }
    }

    /**
     * setup leak canary library
     * @return true if the lib is analysing heap dump, so the app should not be initialized
     * otherwise return false everything can go to normal
     */
    private fun setupLeaksCanary(): Boolean{
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true
        }
        LeakCanary.install(this)
        // Normal app init code...
        return false
    }
}