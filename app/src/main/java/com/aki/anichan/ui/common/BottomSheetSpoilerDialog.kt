package com.aki.anichan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.databinding.DialogBottomSheetSpoilerBinding
import com.aki.anichan.helper.utils.MarkdownUtil
import com.aki.anichan.ui.base.BaseDialogFragment

class BottomSheetSpoilerDialog : BaseDialogFragment<DialogBottomSheetSpoilerBinding>() {

    private var spoilerText = ""
    private var listener: SpoilerListener? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetSpoilerBinding {
        return DialogBottomSheetSpoilerBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        val markdownSetup = if (listener != null) {
            MarkdownUtil.getMarkdownSetup(requireContext(), screenWidth) {
                listener?.onLinkClick(it)
            }
        } else {
            MarkdownUtil.getMarkdownSetup(requireContext(), screenWidth, null)
        }

        MarkdownUtil.applyMarkdown(requireContext(), markdownSetup, binding.dialogSpoilerText, spoilerText)
    }

    override fun setUpObserver() {
        // do nothing
    }

    companion object {
        fun newInstance(spoilerText: String, listener: SpoilerListener?) =
            BottomSheetSpoilerDialog().apply {
                this.spoilerText = spoilerText
                this.listener = listener
            }
    }

    interface SpoilerListener {
        fun onLinkClick(link: String)
    }
}