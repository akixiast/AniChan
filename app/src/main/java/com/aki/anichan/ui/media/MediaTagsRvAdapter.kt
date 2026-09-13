package com.aki.anichan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.response.anilist.MediaTag
import com.aki.anichan.databinding.ListMediaTagBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.getAttrValue
import com.aki.anichan.helper.extensions.getNumberFormatting
import com.aki.anichan.type.MediaType
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class MediaTagsRvAdapter(
    private val context: Context,
    list: List<MediaTag>,
    private val mediaType: MediaType,
    private val listener: MediaListener.MediaTagsListener
) : BaseRecyclerViewAdapter<MediaTag, ListMediaTagBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaTagBinding) : ViewHolder(binding) {
        override fun bind(item: MediaTag, index: Int) {
            binding.apply {
                tagName.text = item.name
                tagRank.text = item.rank.getNumberFormatting() + "%"

                if (item.isGeneralSpoiler || item.isMediaSpoiler) {
                    tagName.setTextColor(context.getAttrValue(R.attr.themeNegativeColor))
                    tagRank.setTextColor(context.getAttrValue(R.attr.themeNegativeColor))
                } else {
                    tagName.setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                    tagRank.setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                }

                tagLayout.clicks {
                    listener.navigateToExplore(mediaType, item)
                }

                tagLayout.setOnLongClickListener {
                    listener.showDescription(item)
                    true
                }
            }
        }
    }
}