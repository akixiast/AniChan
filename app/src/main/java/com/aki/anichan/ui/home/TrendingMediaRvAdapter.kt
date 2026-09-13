package com.aki.anichan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.databinding.ListMediaTrendingBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.getNumberFormatting
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import com.aki.anichan.ui.common.GenreRvAdapter
import com.aki.anichan.type.MediaType

class TrendingMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: HomeListener.TrendingMediaListener,
    private val cardScale: Double = 1.3
) : BaseRecyclerViewAdapter<Media, ListMediaTrendingBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / cardScale).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaTrendingBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                if (item.bannerImage.isNullOrBlank())
                    ImageUtil.loadImage(context, item.getCoverImage(appSetting), trendingBannerImage)
                else
                    ImageUtil.loadImage(context, item.bannerImage, trendingBannerImage)
                ImageUtil.loadImage(context, item.getCoverImage(appSetting), trendingCoverImage)

                trendingMediaTitleText.text = item.getTitle(appSetting)
                trendingMediaProducerText.text = if (item.type == MediaType.ANIME) {
                    item.studios.edges
                        .filter { it.isMain }
                        .joinToString(", ") { it.node.name }
                } else {
                    item.getMainStaff()
                        .joinToString(", ") { it.node.name.full }
                }
                trendingMediaScoreText.text = item.averageScore.getNumberFormatting()
                trendingMediaFavouriteText.text = item.favourites.getNumberFormatting()

                trendingMediaGenreRecyclerView.adapter = GenreRvAdapter(context, item.genres.take(3)) // taking only 3 to avoid text getting cut off
                (trendingMediaGenreRecyclerView.layoutManager as? FlexboxLayoutManager)?.let {
                    it.flexDirection = FlexDirection.ROW
                    it.flexWrap = FlexWrap.WRAP
                    it.maxLine = 2
                }

                root.clicks { listener.navigateToMedia(item) }
            }
        }
    }
}