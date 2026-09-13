package com.aki.anichan.ui.studio.media

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.MediaEdge
import com.aki.anichan.databinding.ListCardImageAndTextBinding
import com.aki.anichan.databinding.ListLoadingBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.getNumberFormatting
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.helper.utils.SpaceItemDecoration
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import com.aki.anichan.type.MediaSort

class StudioMediaListRvAdapter(
    private val context: Context,
    list: List<MediaEdge?>,
    private val appSetting: AppSetting,
    private val mediaSort: MediaSort,
    private val listener: StudioMediaListListener
) : BaseRecyclerViewAdapter<MediaEdge?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListCardImageAndTextBinding.inflate(inflater, parent, false)
                view.cardRecyclerView.addItemDecoration(
                    SpaceItemDecoration(bottom = context.resources.getDimensionPixelSize(
                        R.dimen.marginSmall))
                )
                ItemViewHolder(view)
            }
        }

    }

    inner class ItemViewHolder(private val binding: ListCardImageAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaEdge?, index: Int) {
            if (item == null)
                return

            binding.apply {
                ImageUtil.loadImage(context, item.node.getCoverImage(appSetting), cardImage)

                cardText.text = item.node.getTitle(appSetting)
                cardText.setLines(2)
                cardText.maxLines = 2

                cardSubtitle.show(false)

                cardInfoLayout.show(true)
                cardInfoText.text = item.node.getFormattedMediaFormat(true)

                cardExtraInfoLayout.show(
                    listOf(
                        MediaSort.POPULARITY_DESC,
                        MediaSort.START_DATE_DESC,
                        MediaSort.START_DATE,
                        MediaSort.FAVOURITES_DESC,
                        MediaSort.SCORE_DESC
                    ).any {
                        it == mediaSort
                    }
                )
                cardExtraInfoIcon.show(
                    listOf(
                        MediaSort.POPULARITY_DESC,
                        MediaSort.FAVOURITES_DESC,
                        MediaSort.SCORE_DESC
                    ).any {
                        it == mediaSort
                    }
                )
                when (mediaSort) {
                    MediaSort.POPULARITY_DESC -> {
                        ImageUtil.loadImage(context, R.drawable.ic_calculator, cardExtraInfoIcon)
                        cardExtraInfoIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brightGreen))
                        cardExtraInfoText.text = item.node.popularity.getNumberFormatting()
                    }
                    MediaSort.FAVOURITES_DESC -> {
                        ImageUtil.loadImage(context, R.drawable.ic_heart_outline, cardExtraInfoIcon)
                        cardExtraInfoIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brightRed))
                        cardExtraInfoText.text = item.node.favourites.getNumberFormatting()
                    }
                    MediaSort.SCORE_DESC -> {
                        ImageUtil.loadImage(context, R.drawable.ic_star_filled, cardExtraInfoIcon)
                        cardExtraInfoIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brightYellow))
                        cardExtraInfoText.text = item.node.averageScore.getNumberFormatting()
                    }
                    MediaSort.START_DATE_DESC, MediaSort.START_DATE -> {
                        cardExtraInfoText.text = item.node.startDate?.year?.toString() ?: "TBA"
                    }
                    else -> {
                        cardExtraInfoText.text = ""
                    }
                }

                cardRecyclerView.show(false)

                root.clicks {
                    listener.navigateToMedia(item.node)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: MediaEdge?, index: Int) {
            // do nothing
        }
    }

    interface StudioMediaListListener {
        fun navigateToMedia(media: Media)
    }
}