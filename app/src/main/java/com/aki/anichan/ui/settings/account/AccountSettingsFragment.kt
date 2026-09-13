package com.aki.anichan.ui.settings.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.databinding.FragmentAccountSettingsBinding
import com.aki.anichan.helper.extensions.applyBottomPaddingInsets
import com.aki.anichan.helper.extensions.applySidePaddingInsets
import com.aki.anichan.helper.extensions.applyTopPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.base.NavigationManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountSettingsFragment : BaseFragment<FragmentAccountSettingsBinding, AccountSettingsViewModel>() {

    override val viewModel: AccountSettingsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSettingsBinding {
        return FragmentAccountSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.account_settings))

            accountSettingsUpdateProfileLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_PROFILE_SETTINGS)
            }

            accountSettingsUpdateAccountLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS)
            }

            accountSettingsForceUpdateStatsLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_LISTS_SETTINGS)
            }

            accountSettingsImportListsLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_IMPORT_LISTS)
            }

            accountSettingsConnectWithTwitterLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_CONNECT_WITH_TWITTER)
            }

            accountSettingsDeleteAccountLayout.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS)
            }

            accountSettingsLoginButton.text = getString(R.string.login_with_anilist)
            accountSettingsLoginButton.clicks {
                navigation.navigateToLogin()
            }

            accountSettingsLogoutButton.text = getString(R.string.logout)
            accountSettingsLogoutButton.clicks {
                dialog.showConfirmationDialog(
                    R.string.logout_from_alchan,
                    R.string.logging_out_from_alchan_doesnt_mean_logging_out_from_anilist,
                    R.string.logout,
                    {
                        viewModel.logout()
                        navigation.navigateToSplash()
                    },
                    R.string.cancel,
                    { }
                )
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.accountSettingsLayout.applySidePaddingInsets()
        binding.accountSettingsLoginLogoutLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.isLoggedIn
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoggedIn ->
                    binding.accountSettingsLoginButton.visibility = if (isLoggedIn) android.view.View.GONE else android.view.View.VISIBLE
                    binding.accountSettingsLogoutButton.visibility = if (isLoggedIn) android.view.View.VISIBLE else android.view.View.GONE
                }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()
    }
}