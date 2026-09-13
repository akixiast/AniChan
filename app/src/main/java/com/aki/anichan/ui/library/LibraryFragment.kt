package com.aki.anichan.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aki.anichan.R
import com.aki.anichan.data.manager.UserManager
import com.aki.anichan.databinding.FragmentLibraryBinding
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.extensions.applyTopPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.ui.base.BaseActivity
import com.aki.anichan.ui.main.MainViewPagerAdapter
import com.aki.anichan.ui.medialist.MediaListFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val userManager: UserManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isAuthenticated = userManager.isAuthenticated

        binding.apply {
            libraryTabLayout.applyTopPaddingInsets()
            libraryGuestLayout.show(!isAuthenticated)
            libraryTabLayout.show(isAuthenticated)
            libraryViewPager.show(isAuthenticated)

            if (isAuthenticated) {
                val fragments = listOf(
                    MediaListFragment.newInstance(MediaType.ANIME),
                    MediaListFragment.newInstance(MediaType.MANGA)
                )
                libraryViewPager.adapter = MainViewPagerAdapter(
                    childFragmentManager,
                    viewLifecycleOwner.lifecycle,
                    fragments
                )
                TabLayoutMediator(libraryTabLayout, libraryViewPager) { tab, position ->
                    tab.text = getString(if (position == 0) R.string.anime else R.string.manga)
                }.attach()
            } else {
                libraryLoginButton.clicks {
                    (activity as? BaseActivity<*>)?.navigationManager?.navigateToLogin()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}
