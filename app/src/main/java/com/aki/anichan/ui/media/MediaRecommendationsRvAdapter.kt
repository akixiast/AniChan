package com.aki.anichan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.Recommendation
import com.aki.anichan.databinding.ListMediaRecommendationBinding
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.extensions.*
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import com.aki.anichan.type.MediaStatus

class MediaRecommendationsRvAdapter(
    private val context: Context,
    list: List<Recommendation>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener.MediaRecommendationsListener
) : BaseRecyclerViewAdapter<Recommendation, ListMediaRecommendationBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / 1.3).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaRecommendationBinding) : ViewHolder(binding) {
        override fun bind(item: Recommendation, index: Int) {
            binding.apply {
                item.mediaRecommendation?.let {
                    ImageUtil.loadImage(context, it.getCoverImage(appSetting), recommendationCoverImage)
                    recommendationTitleText.text = it.getTitle(appSetting)
                    recommendationYearText.text = it.startDate?.year?.toString() ?: "TBA"
                    recommendationYearText.show(it.startDate?.year != null || it.status == MediaStatus.NOT_YET_RELEASED)
                    recommendationFormatText.text = it.getFormattedMediaFormat(true)
                    recommendationLengthText.text = it.getLength()?.showUnit(context, if (it.type?.getMediaType() == MediaType.ANIME) R.plurals.episode else R.plurals.chapter)
                    recommendationLengthText.show(it.getLength() != null && it.getLength() != 0)
                    recommendationLengthDividerIcon.show(it.getLength() != null && it.getLength() != 0)

                    recommendationRatingText.text = item.rating.getNumberFormatting()
                    recommendationScoreText.text = it.averageScore.getNumberFormatting()
                    recommendationFavoriteText.text = it.favourites.getNumberFormatting()
                    recommendationCardBackground.clicks {
                        listener.navigateToMedia(it)
                    }
                }
            }
        }
    }
}