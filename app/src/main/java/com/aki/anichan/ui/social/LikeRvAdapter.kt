package com.aki.anichan.ui.social

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.data.response.anilist.User
import com.aki.anichan.databinding.ListLikeBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.pojo.ListItem
import com.aki.anichan.helper.utils.ImageUtil
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class LikeRvAdapter(
    private val context: Context,
    list: List<User>,
    private val appSetting: AppSetting,
    private val likeListener: LikeListener
) : BaseRecyclerViewAdapter<User, ListLikeBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListLikeBinding) : ViewHolder(binding) {
        override fun bind(item: User, index: Int) {
            with(binding) {
                ImageUtil.loadCircleImage(context, item.avatar.getImageUrl(appSetting), likeAvatar)
                likeName.text = item.name
                root.clicks {
                    likeListener.navigateToUser(item)
                }
            }
        }
    }

    interface LikeListener {
        fun navigateToUser(user: User)
    }
}