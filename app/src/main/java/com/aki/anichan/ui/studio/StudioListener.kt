package com.aki.anichan.ui.studio

import com.aki.anichan.data.response.anilist.Media

interface StudioListener {
    fun navigateToMedia(media: Media)
    fun navigateToStudioMedia()
}