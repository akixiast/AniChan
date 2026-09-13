package com.aki.anichan.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.data.response.anilist.Studio
import com.aki.anichan.databinding.ListCardTextBinding
import com.aki.anichan.databinding.ListCircularBinding
import com.aki.anichan.databinding.ListRectangleBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class FavoriteStudioRvAdapter(
    private val context: Context,
    list: List<Studio>,
    private val listener: ProfileListener.FavoriteStudioListener
) : BaseRecyclerViewAdapter<Studio, ListCardTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCardTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCardTextBinding) : ViewHolder(binding) {
        override fun bind(item: Studio, index: Int) {
            binding.apply {
                cardIcon.show(false)
                cardText.text = item.name
                root.clicks { listener.navigateToStudio(item) }
            }
        }
    }
}