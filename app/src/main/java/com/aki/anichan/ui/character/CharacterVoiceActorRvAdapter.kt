package com.aki.anichan.ui.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.R
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.StaffRoleType
import com.aki.anichan.databinding.ListCircularBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class CharacterVoiceActorRvAdapter(
    private val context: Context,
    list: List<StaffRoleType>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: CharacterListener
) : BaseRecyclerViewAdapter<StaffRoleType, ListCircularBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCircularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.root.layoutParams.width = (width.toDouble() / context.resources.getInteger(R.integer.horizontalListCharacterDivider)).toInt()
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCircularBinding) : ViewHolder(binding) {
        override fun bind(item: StaffRoleType, index: Int) {
            binding.apply {
                ImageUtil.loadCircleImage(context, item.voiceActor.getImage(appSetting), circularItemImage)
                circularItemText.text = item.voiceActor.name.userPreferred
                circularItemText.setLines(1)
                circularItemText.maxLines = 1

                circularItemDescriptionText.text = item.voiceActor.language
                circularItemDescriptionText.show(true)
                circularItemDescriptionText.setLines(1)
                circularItemDescriptionText.maxLines = 1

                root.clicks {
                    listener.navigateToStaff(item.voiceActor)
                }

                root.setOnLongClickListener {
                    listener.showStaffMedia(item.voiceActor)
                    true
                }
            }
        }
    }
}