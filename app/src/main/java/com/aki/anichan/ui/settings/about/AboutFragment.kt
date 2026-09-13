package com.aki.anichan.ui.settings.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aki.anichan.BuildConfig
import com.aki.anichan.R
import com.aki.anichan.databinding.FragmentAboutBinding
import com.aki.anichan.helper.extensions.applyBottomSidePaddingInsets
import com.aki.anichan.helper.extensions.applyTopPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.service.update.AppUpdateDownloader
import com.aki.anichan.helper.service.update.AppUpdateInfo
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel>() {

    override val viewModel: AboutViewModel by viewModel()

    private var pendingUpdateInfo: AppUpdateInfo? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutBinding {
        return FragmentAboutBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.about_al_chan))

            aboutSettingsAppVersionText.text = getString(R.string.version, BuildConfig.VERSION_NAME)

            aboutSettingsAniListLink.clicks {
                navigation.openWebView(NavigationManager.Url.ANILIST_WEBSITE)
            }

            aboutSettingsGitHubLink.clicks {
                navigation.openWebView(NavigationManager.Url.ALCHAN_GITHUB)
            }

            aboutSettingsCheckUpdateButton.clicks {
                viewModel.checkForUpdate(manual = true)
            }

            aboutSettingsDownloadUpdateButton.clicks {
                pendingUpdateInfo?.let { info ->
                    AppUpdateDownloader.startDownload(
                        requireContext(),
                        info.apkDownloadUrl,
                        "AniChan-${info.latestTag}.apk"
                    )
                    dialog.showToast(getString(R.string.downloading_update))
                }
            }

            aboutSettingsReleaseNotesButton.clicks {
                pendingUpdateInfo?.let { info ->
                    navigation.openWebView(info.releasePageUrl)
                }
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.aboutSettingsLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.updateState.subscribe { state ->
                binding.apply {
                    when (state) {
                        is AboutViewModel.UpdateState.Checking -> {
                            aboutSettingsUpdateStatusText.text = getString(R.string.checking_for_updates)
                            aboutSettingsDownloadUpdateButton.show(false)
                            aboutSettingsReleaseNotesButton.show(false)
                        }
                        is AboutViewModel.UpdateState.Available -> {
                            pendingUpdateInfo = state.info
                            aboutSettingsUpdateStatusText.text =
                                getString(R.string.new_version_available, state.info.releaseName)
                            aboutSettingsDownloadUpdateButton.show(true)
                            aboutSettingsReleaseNotesButton.show(true)
                        }
                        is AboutViewModel.UpdateState.UpToDate -> {
                            pendingUpdateInfo = null
                            aboutSettingsUpdateStatusText.text =
                                getString(R.string.app_is_up_to_date, state.latestTag)
                            aboutSettingsDownloadUpdateButton.show(false)
                            aboutSettingsReleaseNotesButton.show(false)
                        }
                        is AboutViewModel.UpdateState.Error -> {
                            aboutSettingsUpdateStatusText.text = getString(R.string.update_check_failed)
                            aboutSettingsDownloadUpdateButton.show(false)
                            aboutSettingsReleaseNotesButton.show(false)
                        }
                    }
                }
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }
}