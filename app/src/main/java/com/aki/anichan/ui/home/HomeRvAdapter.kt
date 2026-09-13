package com.aki.anichan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.databinding.LayoutHomeHeaderBinding
import com.aki.anichan.databinding.LayoutHomeMenuBinding
import com.aki.anichan.databinding.LayoutHomeReleasingTodayBinding
import com.aki.anichan.databinding.LayoutHomeSocialBinding
import com.aki.anichan.databinding.LayoutHomeTrendingBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.pojo.HomeItem
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class HomeRvAdapter(
    private val context: Context,
    list: List<HomeItem>,
    private val user: User?,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: HomeListener
) : BaseRecyclerViewAdapter<HomeItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            HomeItem.VIEW_TYPE_HEADER -> {
                val view = LayoutHomeHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(view)
            }
            HomeItem.VIEW_TYPE_MENU -> {
                val view = LayoutHomeMenuBinding.inflate(inflater, parent, false)
                return MenuViewHolder(view)
            }
            HomeItem.VIEW_TYPE_RELEASING_TODAY -> {
                val view = LayoutHomeReleasingTodayBinding.inflate(inflater, parent, false)
                return ReleasingTodayViewHolder(view)
            }
            HomeItem.VIEW_TYPE_SOCIAL -> {
                val view = LayoutHomeSocialBinding.inflate(inflater, parent, false)
                return SocialViewHolder(view)
            }
            HomeItem.VIEW_TYPE_TRENDING_ANIME, HomeItem.VIEW_TYPE_TRENDING_MANGA,
            HomeItem.VIEW_TYPE_POPULAR_ANIME, HomeItem.VIEW_TYPE_POPULAR_MANGA,
            HomeItem.VIEW_TYPE_TOP_ANIME, HomeItem.VIEW_TYPE_TOP_MANGA,
            HomeItem.VIEW_TYPE_CURRENT_SEASON, HomeItem.VIEW_TYPE_NEXT_SEASON,
            HomeItem.VIEW_TYPE_NEW_ANIME, HomeItem.VIEW_TYPE_NEW_MANGA -> {
                val view = LayoutHomeTrendingBinding.inflate(inflater, parent, false)
                return TrendingMediaViewHolder(view)
            }
            else -> {
                val view = LayoutHomeHeaderBinding.inflate(inflater, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class HeaderViewHolder(private val binding: LayoutHomeHeaderBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                if (user?.bannerImage?.isNotBlank() == true)
                    ImageUtil.loadImage(context, user.bannerImage, headerImage)
                else
                    ImageUtil.loadImage(context, 0, headerImage)
            }
        }
    }

    inner class MenuViewHolder(private val binding: LayoutHomeMenuBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                seasonalMenu.clicks { listener.menuListener.navigateToSeasonal() }
                exploreMenu.clicks { listener.menuListener.showExploreDialog() }
                reviewsMenu.clicks { listener.menuListener.navigateToReview() }
                calendarMenu.clicks { listener.menuListener.navigateToCalendar() }
            }
        }
    }

    inner class ReleasingTodayViewHolder(private val binding: LayoutHomeReleasingTodayBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            with(binding) {
                if (item.releasingToday.isNotEmpty()) {
                    releasingTodayRecyclerView.adapter = ReleasingTodayRvAdapter(context, item.releasingToday, appSetting, width, listener.releasingTodayListener)
                    releasingTodayRecyclerView.show(true)
                    releasingTodayEmptyText.show(false)
                } else {
                    releasingTodayRecyclerView.show(false)
                    releasingTodayEmptyText.show(true)
                }
            }
        }
    }

    inner class SocialViewHolder(private val binding: LayoutHomeSocialBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                homeSocialJoinButton.clicks { listener.socialListener.navigateToSocial() }
                root.clicks { listener.socialListener.navigateToSocial() }
            }
        }
    }

    inner class TrendingMediaViewHolder(private val binding: LayoutHomeTrendingBinding) : ViewHolder(binding) {
        override fun bind(item: HomeItem, index: Int) {
            binding.apply {
                trendingRightNowText.text = when (item.viewType) {
                    HomeItem.VIEW_TYPE_TRENDING_ANIME -> context.getString(R.string.trending_anime_right_now)
                    HomeItem.VIEW_TYPE_TRENDING_MANGA -> context.getString(R.string.trending_manga_right_now)
                    HomeItem.VIEW_TYPE_POPULAR_ANIME -> context.getString(R.string.popular_anime)
                    HomeItem.VIEW_TYPE_POPULAR_MANGA -> context.getString(R.string.popular_manga)
                    HomeItem.VIEW_TYPE_TOP_ANIME -> context.getString(R.string.top_anime)
                    HomeItem.VIEW_TYPE_TOP_MANGA -> context.getString(R.string.top_manga)
                    HomeItem.VIEW_TYPE_CURRENT_SEASON -> context.getString(R.string.current_season_anime)
                    HomeItem.VIEW_TYPE_NEXT_SEASON -> context.getString(R.string.next_season_anime)
                    HomeItem.VIEW_TYPE_NEW_ANIME -> context.getString(R.string.new_anime)
                    HomeItem.VIEW_TYPE_NEW_MANGA -> context.getString(R.string.new_manga)
                    else -> ""
                }

                if (item.media.isNotEmpty()) {
                    // top rails get slightly bigger cards than the rest
                    val scale = if (item.viewType == HomeItem.VIEW_TYPE_TOP_ANIME || item.viewType == HomeItem.VIEW_TYPE_TOP_MANGA) 1.7 else 2.1
                    trendingListRecyclerView.adapter = TrendingMediaRvAdapter(context, item.media, appSetting, width, listener.trendingMediaListener, scale)
                    trendingProgressBar.show(false)
                } else {
                    trendingProgressBar.show(true)
                }
            }
        }
    }
}