package com.aki.anichan.ui.seasonal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.databinding.FragmentSeasonalBinding
import com.aki.anichan.helper.enums.ListType
import com.aki.anichan.helper.enums.getString
import com.aki.anichan.helper.enums.getStringResource
import com.aki.anichan.helper.extensions.*
import com.aki.anichan.helper.pojo.MediaListItem
import com.aki.anichan.helper.pojo.SeasonalAdapterComponent
import com.aki.anichan.ui.base.BaseFragment
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeasonalFragment : BaseFragment<FragmentSeasonalBinding, SeasonalViewModel>() {

    override val viewModel: SeasonalViewModel by viewModel()

    private var menuItemChangeListType: MenuItem? = null

    private var adapter: BaseSeasonalRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSeasonalBinding {
        return FragmentSeasonalBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.seasonal_chart))

            defaultToolbar.defaultToolbar.apply {
                inflateMenu(R.menu.menu_seasonal)
                menuItemChangeListType = menu.findItem(R.id.itemChangeListType)
            }

            menuItemChangeListType?.setOnMenuItemClickListener {
                viewModel.loadListTypeList()
                true
            }

            assignAdapter(SeasonalAdapterComponent())

            seasonalSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            seasonalYearText.clicks {
                viewModel.loadYearList()
            }

            seasonalSeasonText.clicks {
                viewModel.loadSeasonList()
            }

            seasonalSortText.clicks {
                viewModel.loadSortList()
            }

            seasonalOrderByText.clicks {
                viewModel.loadOrderByList()
            }

            seriesHideSeriesCheckBox.setOnClickListener {
                viewModel.updateHideSeriesOnList(seriesHideSeriesCheckBox.isChecked)
            }

            seriesShowSeriesCheckBox.setOnClickListener {
                viewModel.updateOnlyShowSeriesOnList(seriesShowSeriesCheckBox.isChecked)
            }

            seriesShowAdultContentCheckBox.setOnClickListener {
                viewModel.updateShowAdultContent(seriesShowAdultContentCheckBox.isChecked)
            }
        }
    }

    override fun setUpInsets() {
        with(binding) {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            seasonalRecyclerView.applySidePaddingInsets()
            seasonalFilterLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
                binding.seasonalSwipeRefresh.isRefreshing = false
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.seasonalAdapterComponent.subscribe {
                assignAdapter(it)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.seasonalItems.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.year.subscribe {
                binding.seasonalYearText.text = it.toString()
            },
            viewModel.season.subscribe {
                binding.seasonalSeasonText.text = it.getString()
            },
            viewModel.sort.subscribe {
                binding.seasonalSortText.text = getString(it.getStringResource())
            },
            viewModel.orderByDescending.subscribe {
                binding.seasonalOrderByText.text = getString(if (it) R.string.descending else R.string.ascending)
            },
            viewModel.hideSeriesOnList.subscribe {
                binding.seriesHideSeriesCheckBox.isChecked = it
            },
            viewModel.onlyShowSeriesOnList.subscribe {
                binding.seriesShowSeriesCheckBox.isChecked = it
            },
            viewModel.showAdult.subscribe {
                binding.seriesShowAdultContentCheckBox.isChecked = it
            },
            viewModel.yearList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateYear(data)
                }
            },
            viewModel.seasonList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateSeason(data)
                }
            },
            viewModel.sortList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateSort(data)
                }
            },
            viewModel.orderByList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateOrderBy(data)
                }
            },
            viewModel.listTypeList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateListType(data)
                }
            }
        )

        viewModel.loadData(Unit)
    }

    private fun assignAdapter(seasonalAdapterComponent: SeasonalAdapterComponent) {
        val useGrid = seasonalAdapterComponent.listType == ListType.GRID

        if (useGrid) {
            adapter = SeasonalGridRvAdapter(requireContext(), listOf(), seasonalAdapterComponent.appSetting, getSeasonalListener())
            binding.seasonalRecyclerView.layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.gridSpan))
        } else {
            adapter = SeasonalLinearRvAdapter(requireContext(), listOf(), seasonalAdapterComponent.appSetting, getSeasonalListener())
            binding.seasonalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        (binding.seasonalRecyclerView.layoutManager as? GridLayoutManager)?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItemViewType(position) == MediaListItem.VIEW_TYPE_TITLE) resources.getInteger(R.integer.gridSpan) else 1
            }
        }

        binding.seasonalRecyclerView.adapter = adapter
    }

    private fun getSeasonalListener(): BaseSeasonalRvAdapter.SeasonalListener {
        return object : BaseSeasonalRvAdapter.SeasonalListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun showQuickDetail(media: Media) {
                dialog.showMediaQuickDetailDialog(media)
            }

            override fun addToPlanning(media: Media) {
                viewModel.addToPlanning(media)
            }

            override fun watchTrailer(media: Media) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        menuItemChangeListType = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SeasonalFragment()
    }
}