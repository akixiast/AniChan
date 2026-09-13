package com.aki.anichan.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.databinding.FragmentDiscoverBinding
import com.aki.anichan.helper.extensions.applyTopPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment : BaseFragment<FragmentDiscoverBinding, DiscoverViewModel>() {

    override val viewModel: DiscoverViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDiscoverBinding {
        return FragmentDiscoverBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            discoverSearchCard.clicks { viewModel.loadSearchCategories() }
            discoverSeasonalCard.clicks { navigation.navigateToSeasonal() }
            discoverExploreCard.clicks { viewModel.loadExploreCategories() }
            discoverCalendarCard.clicks { navigation.navigateToCalendar() }
            discoverReviewsCard.clicks { navigation.navigateToReview() }
            discoverSocialCard.clicks { navigation.navigateToSocial() }
        }
    }

    override fun setUpInsets() {
        binding.discoverScrollView.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.searchCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    navigation.navigateToSearch(data)
                }
            },
            viewModel.exploreCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    navigation.navigateToExplore(data)
                }
            }
        )

        if (!sharedDisposablesAdded) {
            sharedDisposables.add(
                sharedViewModel.getScrollToTopObservable(SharedMainViewModel.Page.DISCOVER).subscribe {
                    binding.discoverScrollView.smoothScrollTo(0, 0)
                }
            )
            sharedDisposablesAdded = true
        }

        viewModel.loadData(Unit)
    }

    companion object {
        @JvmStatic
        fun newInstance() = DiscoverFragment()
    }
}
