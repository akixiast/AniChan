package com.aki.anichan.data.response.anilist

import com.aki.anichan.data.entity.AppSetting
import com.aki.anichan.type.NotificationType

data class ThreadCommentReplyNotification(
    override val id: Int = 0,
    val userId: Int = 0,
    override val type: NotificationType = NotificationType.THREAD_COMMENT_REPLY,
    val commentId: Int = 0,
    val context: String = "",
    override val createdAt: Int = 0,
    val thread: Thread = Thread(),
    val comment: ThreadComment = ThreadComment(),
    val user: User = User()
) : Notification {
    override fun getMessage(appSetting: AppSetting): String {
        return "${user.name}${context}${thread.title}"
    }
}