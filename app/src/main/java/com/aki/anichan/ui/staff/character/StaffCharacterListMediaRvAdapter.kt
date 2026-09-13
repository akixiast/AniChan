package com.aki.anichan.ui.staff.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Media
import com.aki.anichan.databinding.ListStaffCharacterMediaBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import com.aki.anichan.ui.staff.StaffListener

class StaffCharacterListMediaRvAdapter(
    private val context: Context,
    list: List<Media>,
    private val appSetting: AppSetting,
    private val listener: StaffCharacterListListener
) : BaseRecyclerViewAdapter<Media, ListStaffCharacterMediaBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListStaffCharacterMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListStaffCharacterMediaBinding) : ViewHolder(binding) {
        override fun bind(item: Media, index: Int) {
            binding.apply {
                mediaName.text = item.getTitle(appSetting)
                ImageUtil.loadRectangleImage(context, item.getCoverImage(appSetting), mediaCoverImage)
                root.clicks { listener.navigateToMedia(item) }
            }
        }
    }
}