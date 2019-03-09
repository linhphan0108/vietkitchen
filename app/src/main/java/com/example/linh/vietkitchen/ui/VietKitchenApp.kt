package com.example.linh.vietkitchen.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDexApplication
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.di.ApplicationComponent
import com.example.linh.vietkitchen.di.DaggerApplicationComponent
import com.example.linh.vietkitchen.di.DaggerComponentProvider
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.util.NotLoggingTree
import com.google.firebase.auth.FirebaseAuth
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber



class VietKitchenApp : MultiDexApplication(), DaggerComponentProvider{

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .application(this)
                .build()
    }

    companion object {
        private  val _userInfo: MutableLiveData<UserInfo> = MutableLiveData()
        private val _category: MutableLiveData<List<DrawerNavGroupItem>> = MutableLiveData()
        val category: LiveData<List<DrawerNavGroupItem>> = _category

        fun getUserInfo() = _userInfo.value!!
        fun getUserInfoObservable(): LiveData<UserInfo> = _userInfo
        fun removeLikedRecipeId(id: String){
            val removed = getUserInfo().likedRecipesIds?.remove(id) ?: false
            if(removed) {
                getUserInfo().numberFavoriteRecipes = getUserInfo().likedRecipesIds?.size ?: 0
                _userInfo.postValue(getUserInfo())
            }
        }
        fun addLikedRecipeId(id: String) {
            val added = getUserInfo().likedRecipesIds?.add(id) ?: false
            if (added) {
                getUserInfo().numberFavoriteRecipes = getUserInfo().likedRecipesIds?.size ?: 0
                _userInfo.postValue(getUserInfo())
            }
        }

        fun setCategory(newCat: List<DrawerNavGroupItem>){
            _category.postValue(newCat)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initUserInfo()
        if(setupLeaksCanary()) return
        setupTimberLogger()
    }

    fun initUserInfo(){
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            _userInfo.value = UserInfo(currentUser.uid, currentUser.displayName, currentUser.email, currentUser.photoUrl)
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