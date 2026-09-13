package com.aki.anichan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.Character
import com.aki.anichan.data.response.anilist.Staff
import com.aki.anichan.data.response.anilist.StaffEdge
import com.aki.anichan.databinding.ListCircularBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class MediaStaffRvAdapter(
    private val context: Context,
    list: List<StaffEdge>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener.MediaStaffListener
) : BaseRecyclerViewAdapter<StaffEdge, ListCircularBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / context.resources.getInteger(R.integer.horizontalListCharacterDivider)).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCircularBinding) : ViewHolder(binding) {
        override fun bind(item: StaffEdge, index: Int) {
            binding.apply {
                ImageUtil.loadCircleImage(context, item.node.getImage(appSetting), circularItemImage)
                circularItemText.text = item.node.name.userPreferred
                circularItemText.setLines(1)
                circularItemText.maxLines = 1
                circularItemDescriptionText.text = item.role
                circularItemDescriptionText.show(true)

                root.clicks { listener.navigateToStaff(item.node) }
            }
        }
    }
}