package com.aki.anichan.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.MediaList
import com.aki.anichan.databinding.ListMediaListGridBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.pojo.ReleasingTodayItem
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.helper.utils.TimeUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import com.aki.anichan.type.MediaType
import kotlin.math.abs

class ReleasingTodayRvAdapter(
    private val context: Context,
    list: List<ReleasingTodayItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: HomeListener.ReleasingTodayListener
) : BaseRecyclerViewAdapter<ReleasingTodayItem, ListMediaListGridBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaListGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // compact rail card: ~3.2 visible per screen, same row style as other rails
        view.root.layoutParams.width = (width.toDouble() / 3.2).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaListGridBinding) : ViewHolder(binding) {
        override fun bind(item: ReleasingTodayItem, index: Int) {
            with(binding) {
                val mediaList = item.mediaList
                val media = mediaList.media

                ImageUtil.loadImage(context, media.getCoverImage(appSetting), mediaListCoverImage)

                mediaListTitleText.text = media.getTitle(appSetting)

                mediaListFormatText.text = if (item.timeUntilAiring >= 0) {
                    if (item.timeUntilAiring >= 3600) {
                        context.getString(R.string.ep_x_in_y_hours, item.episode, item.timeUntilAiring / 3600)
                    } else {
                        context.getString(R.string.ep_x_in_y_minutes, item.episode, item.timeUntilAiring / 60)
                    }
                } else {
                    if (item.timeUntilAiring <= -3600) {
                        context.getString(R.string.ep_x_y_hours_ago, item.episode, abs(item.timeUntilAiring) / 3600)
                    } else {
                        context.getString(R.string.ep_x_y_minutes_ago, item.episode, abs(item.timeUntilAiring) / 60)
                    }
                }
                mediaListProgressText.text = "${mediaList.progress} / ${mediaList.media.episodes ?: "?"}"

                mediaListAiringRootLayout.show(false)
                mediaListScoreLayout.show(false)
                mediaListProgressVolumeLayout.show(false)

                root.clicks {
                    listener.navigateToListEditor(mediaList)
                }

                mediaListTitleLayout.clicks {
                    listener.navigateToMedia(media)
                }

                mediaListProgressLayout.clicks {
                    listener.showProgressDialog(mediaList)
                }
            }
        }
    }
}