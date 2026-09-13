package com.aki.anichan.ui.common

import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.databinding.DialogBottomSheetTextInputBinding
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.helper.extensions.show
import com.aki.anichan.helper.pojo.TextInputSetting
import com.aki.anichan.ui.base.BaseDialogFragment

class BottomSheetTextInputDialog : BaseDialogFragment<DialogBottomSheetTextInputBinding>() {

    private var listener: BottomSheetTextInputListener? = null
    private var currentText = ""
    private var textInputSetting = TextInputSetting()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetTextInputBinding {
        return DialogBottomSheetTextInputBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            // Need to do this kind of handling because
            // SwiftKey keyboard ignores inputType set through code
            val dialogEditText = when (textInputSetting.inputType) {
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES -> dialogCapSentencesEditText
                InputType.TYPE_TEXT_FLAG_CAP_WORDS -> dialogCapWordsEditText
                InputType.TYPE_CLASS_NUMBER -> dialogNumberEditText
                InputType.TYPE_TEXT_VARIATION_URI -> dialogUriEditText
                else -> dialogCapSentencesEditText
            }

            dialogEditText.show(true)
            dialogEditText.setText(currentText)

            dialogEditText.apply {
                isSingleLine = textInputSetting.singleLine
                filters = arrayOf(InputFilter.LengthFilter(textInputSetting.characterLimit))
                hint = getString(textInputSetting.hintStringResource)
                setSelection(0, dialogEditText.text?.length ?: 0)
            }

            dialogSaveButton.clicks {
                val newText = dialogEditText.text?.toString()?.trim() ?: ""
                listener?.getNewText(newText)
                dismiss()
            }

            dialogEditText.requestFocus()
            openKeyboard()
        }
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener = null
    }

    companion object {
        fun newInstance(currentText: String, textInputSetting: TextInputSetting, listener: BottomSheetTextInputListener) = BottomSheetTextInputDialog().apply {
            this.currentText = currentText
            this.textInputSetting = textInputSetting
            this.listener = listener
        }
    }

    interface BottomSheetTextInputListener {
        fun getNewText(newText: String)
    }
}