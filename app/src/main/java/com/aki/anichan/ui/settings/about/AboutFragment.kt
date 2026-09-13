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
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.base.NavigationManager
import org.koin.androidx.viewmodel.ext.android.viewModel


class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel>() {

    override val viewModel: AboutViewModel by viewModel()

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
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.aboutSettingsLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        @JvmStatic
        fun newInstance() = AboutFragment()
    }
}