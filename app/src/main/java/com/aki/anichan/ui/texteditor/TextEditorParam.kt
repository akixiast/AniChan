package com.aki.anichan.ui.texteditor

import com.aki.anichan.helper.enums.TextEditorType

data class TextEditorParam(
    val textEditorType: TextEditorType,
    val activityId: Int? = null,
    val activityReplyId: Int? = null,
    val recipientId: Int? = null,
    val username: String? = null
)
