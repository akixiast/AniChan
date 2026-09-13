package com.aki.anichan.ui.landing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.databinding.FragmentLandingBinding
import com.aki.anichan.helper.extensions.applyTopBottomPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class LandingFragment : BaseFragment<FragmentLandingBinding, LandingViewModel>() {

    override val viewModel: LandingViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLandingBinding {
        return FragmentLandingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            ImageUtil.loadImage(requireContext(), R.drawable.login_background, landingBackgroundImage)

            landingGetStartedButton.clicks {
                navigation.navigateToLogin()
            }

            landingSkipButton.clicks {
                viewModel.setGuestMode()
                navigation.navigateToSplash(bypassSplash = false)
            }
        }
    }

    override fun setUpInsets() {
        binding.landingContentRoot.applyTopBottomPaddingInsets()
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        @JvmStatic
        fun newInstance() = LandingFragment()
    }
}