package com.example.linh.vietkitchen.ui.screen.home.profile

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseFragment
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.ui.model.UserInfo
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(), View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModel(this){ injector.profileFragmentViewModel }

    val userInfo by lazy { VietKitchenApp.getUserInfo() }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViews(userInfo)
    }

    //region MVP callbacks
    override fun getFragmentLayoutRes() = R.layout.fragment_profile

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun observeViewModel() {
        viewModel.logoutStatus.observe(this, Observer {box ->
            box?.let {
                when(box.code){
                    Status.SUCCESS -> {onLogoutSuccess()}
                    Status.ERROR -> {onLogoutFailed()}
                }
            }
        })
        VietKitchenApp.getUserInfoObservable().observe(this, Observer {
            bindViews(it)
        })
    }

    fun onLogoutSuccess() {
        activity?.finish()
    }

    fun onLogoutFailed() {
        showSnackBar(view!!, R.string.message_error)
    }
    //endregion MVP callbacks

    //region callbacks==============================================================================
    override fun onClick(v: View) {
        when(v.id){
            R.id.txtLogOut -> {
                showSnackBar(v.rootView, R.string.message_snack_bar_logout, action = getString(R.string.label_logout),
                        listener = View.OnClickListener {
                            viewModel.logout()
                })
            }
        }
    }
    //endregion callbacks

    @SuppressLint("StringFormatMatches")
    private fun bindViews(userInfo: UserInfo) {
        with(userInfo){
            GlideApp.with(context!!)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .apply(RequestOptions.circleCropTransform())
                    .into(img)
            txtDisplayName.text = displayName
            txtEmail.text = email
            txtFavorite.text = getString(R.string.label_favorite_recipes, numberFavoriteRecipes).capitalize()
            swNotification.isChecked = allowNotification
        }
        txtLogOut.setOnClickListener(this)
        swNotification.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onAllowNotificationChanged(isChecked)
        }
    }
}