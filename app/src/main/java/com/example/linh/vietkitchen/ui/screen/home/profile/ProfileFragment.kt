package com.example.linh.vietkitchen.ui.screen.home.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mvpBase.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment<ProfileContractView, ProfileContractPresenter>(), ProfileContractView, View.OnClickListener {
    val userInfo by lazy { VietKitchenApp.userInfo }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViews()
    }

    //region MVP callbacks
    override fun initPresenter() = ProfilePresenter()

    override fun getViewContract() = this

    override fun getFragmentLayoutRes() = R.layout.fragment_profile
    override val viewContext: Context?
        get() = context

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onNoInternetException() {

    }

    override fun onLogoutSuccess() {
        activity?.finish()
    }

    override fun onLogoutFailed() {
        showSnackBar(view!!, R.string.message_error)
    }
    //endregion MVP callbacks

    //region callbacks==============================================================================
    override fun onClick(v: View) {
        when(v.id){
            R.id.txtLogOut -> {
                showSnackBar(v.rootView, R.string.message_snack_bar_logout, action = getString(R.string.label_logout),
                        listener = View.OnClickListener {
                    presenter.logout()
                })
            }
        }
    }
    //endregion callbacks

    @SuppressLint("StringFormatMatches")
    private fun bindViews(){
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
            presenter.onAllowNotificationChanged(isChecked)
        }
    }
}