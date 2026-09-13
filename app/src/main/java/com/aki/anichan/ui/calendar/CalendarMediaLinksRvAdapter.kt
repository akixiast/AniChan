package com.aki.anichan.ui.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.aki.anichan.R
import com.aki.anichan.data.response.anilist.MediaExternalLink
import com.aki.anichan.databinding.ListCalendarMediaLinkBinding
import com.aki.anichan.databinding.ListMediaLinkBinding
import com.aki.anichan.helper.enums.OtherLink
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.getAttrValue
import com.aki.anichan.helper.extensions.getThemeTextColor
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class CalendarMediaLinksRvAdapter(
    private val context: Context,
    list: List<MediaExternalLink>,
    private val listener: CalendarMediaLinksListener
) : BaseRecyclerViewAdapter<MediaExternalLink, ListCalendarMediaLinkBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCalendarMediaLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCalendarMediaLinkBinding) : ViewHolder(binding) {
        override fun bind(item: MediaExternalLink, index: Int) {
            binding.apply {
                if (item.icon.isNotBlank())
                    ImageUtil.loadImage(context, item.icon, linkIcon)
                else
                    ImageUtil.loadImage(context, R.drawable.ic_link, linkIcon)

                if (item.color.isNotBlank()) {
                    val linkColor = Color.parseColor(item.color)
                    val transparentLinkColor = Color.parseColor(item.color.replace("#", "#1F"))
                    linkCard.strokeColor = linkColor
                    linkCard.setCardBackgroundColor(ColorStateList.valueOf(transparentLinkColor))
                    linkName.setTextColor(linkColor)
                    linkIcon.imageTintList = ColorStateList.valueOf(linkColor)
                } else {
                    linkCard.strokeColor = context.getThemeTextColor()
                    linkCard.setCardBackgroundColor(ColorStateList.valueOf(context.getAttrValue(R.attr.themeContentTransparentColor)))
                    linkName.setTextColor(context.getThemeTextColor())
                    linkIcon.imageTintList = ColorStateList.valueOf(context.getThemeTextColor())
                }

                linkName.text = item.getSiteNameWithLanguage()

                linkCard.clicks {
                    listener.navigateToUrl(item)
                }
            }
        }
    }

    interface CalendarMediaLinksListener {
        fun navigateToUrl(mediaExternalLink: MediaExternalLink)
    }
}