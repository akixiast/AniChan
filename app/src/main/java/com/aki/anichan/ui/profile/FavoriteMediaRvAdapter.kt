package com.aki.anichan.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.databinding.ListRectangleBinding
import com.aki.anichan.helper.enums.MediaType
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class FavoriteMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val mediaType: MediaType,
    private val appSetting: AppSetting,
    private val listener: ProfileListener.FavoriteMediaListener
) : BaseRecyclerViewAdapter<Media, ListRectangleBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListRectangleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListRectangleBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                val image = item.getCoverImage(appSetting)
                ImageUtil.loadImage(context, image, rectangleItemImage)
                rectangleItemText.show(false)
                root.clicks { listener.navigateToMedia(item, mediaType) }
            }
        }
    }
}