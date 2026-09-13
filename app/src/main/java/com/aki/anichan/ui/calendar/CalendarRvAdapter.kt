package com.aki.anichan.ui.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.AiringSchedule
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.data.response.anilist.MediaExternalLink
import com.aki.anichan.databinding.ListCalendarBinding
import com.aki.anichan.helper.extensions.*
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.helper.utils.TimeUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class CalendarRvAdapter(
    private val context: Context,
    list: List<AiringSchedule>,
    private val appSetting: AppSetting,
    private val listener: CalendarListener
) : BaseRecyclerViewAdapter<AiringSchedule, ListCalendarBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCalendarBinding) : ViewHolder(binding) {
        override fun bind(item: AiringSchedule, index: Int) {
            with(binding) {
                ImageUtil.loadImage(context, item.media.bannerImage, calendarBannerImage)

                calendarTime.text = TimeUtil.displayInTimeFormat(item.airingAt)

                calendarTitle.text = item.media.getTitle(appSetting)
                calendarRemainingTime.text = if (item.timeUntilAiring > 0) {
                    val secondsUntilAiring = item.timeUntilAiring
                    val remainingTime = when {
                        // more than 1 day
                        secondsUntilAiring > 3600 * 24 -> {
                            secondsUntilAiring.convertSecondsToDays().showUnit(context, R.plurals.day)
                        }
                        // more than 1 hour
                        secondsUntilAiring >= 3600 -> {
                            secondsUntilAiring.convertSecondsToHours().showUnit(context, R.plurals.hour)
                        }
                        else -> {
                            secondsUntilAiring.convertSecondsToMinutes().showUnit(context, R.plurals.minute)
                        }
                    }
                    context.getString(R.string.ep_x_in_y, item.episode, remainingTime).trimEnd('.')
                } else {
                    context.getString(R.string.ep_x, item.episode)
                }

                calendarMediaListStatusLayout.makeVisible(item.media.mediaListEntry != null)
                val statusColor = item.media.mediaListEntry?.status?.getColor()?.let {
                    Color.parseColor(it)
                } ?: context.getThemeSecondaryColor()
                calendarMediaListStatusIcon.imageTintList = ColorStateList.valueOf(statusColor)
                calendarMediaListStatusText.text = item.media.mediaListEntry?.status?.getString(item.media.type?.getMediaType() ?: com.aki.anichan.helper.enums.MediaType.ANIME)?.uppercase()
                calendarMediaListStatusText.setTextColor(statusColor)

                calendarStreamingRecyclerView.show(item.media.externalLinks.isNotEmpty())
                calendarStreamingRecyclerView.adapter = CalendarMediaLinksRvAdapter(context, item.media.externalLinks, object : CalendarMediaLinksRvAdapter.CalendarMediaLinksListener {
                    override fun navigateToUrl(mediaExternalLink: MediaExternalLink) {
                        listener.navigateToUrl(mediaExternalLink)
                    }
                })

                root.clicks {
                    listener.navigateToMedia(item.media)
                }
            }
        }
    }

    interface CalendarListener {
        fun navigateToMedia(media: Media)
        fun navigateToUrl(mediaExternalLink: MediaExternalLink)
    }
}