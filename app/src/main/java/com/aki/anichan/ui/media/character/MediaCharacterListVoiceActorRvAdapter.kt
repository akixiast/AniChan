package com.aki.anichan.ui.media.character

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.StaffRoleType
import com.aki.anichan.databinding.ListMediaCharacterVoiceActorBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class MediaCharacterListVoiceActorRvAdapter(
    private val context: Context,
    list: List<StaffRoleType>,
    private val appSetting: AppSetting,
    private val listener: MediaCharacterListRvAdapter.MediaCharacterListListener
) : BaseRecyclerViewAdapter<StaffRoleType, ListMediaCharacterVoiceActorBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaCharacterVoiceActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaCharacterVoiceActorBinding) : ViewHolder(binding) {
        override fun bind(item: StaffRoleType, index: Int) {
            binding.apply {
                voiceActorName.text = item.voiceActor.name.userPreferred
                voiceActorRoleNote.text = "(${item.roleNote})"
                voiceActorRoleNote.show(item.roleNote.isNotBlank())
                voiceActorDubGroup.text = item.dubGroup
                voiceActorDubGroup.show(item.dubGroup.isNotBlank())
                ImageUtil.loadCircleImage(context, item.voiceActor.getImage(appSetting), voiceActorImage)

                root.clicks { listener.navigateToStaff(item.voiceActor) }
            }
        }
    }
}