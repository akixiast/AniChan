package com.aki.anichan.ui.reorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.aki.anichan.R
import com.aki.anichan.databinding.FragmentReorderBinding
import com.aki.anichan.helper.extensions.applyBottomPaddingInsets
import com.aki.anichan.helper.extensions.applyTopPaddingInsets
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.ui.base.BaseFragment
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReorderFragment : BaseFragment<FragmentReorderBinding, ReorderViewModel>() {

    override val viewModel: ReorderViewModel by viewModel()

    private var listener: ReorderListener? = null

    private var reorderAdapter: ReorderRvAdapter? = null
    private var itemTouchHelper: ItemTouchHelper? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReorderBinding {
        return FragmentReorderBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            defaultToolbar.defaultToolbar.apply {
                setNavigationIcon(R.drawable.ic_left)
                setNavigationOnClickListener { goBack() }
            }

            reorderAdapter = ReorderRvAdapter(listOf(), object : DragListener {
                override fun onStartDrag(viewHolder: BaseRecyclerViewAdapter<*, *>.ViewHolder) {
                    itemTouchHelper?.startDrag(viewHolder)
                }
            })
            itemTouchHelper = ItemTouchHelper(ItemMoveCallback(reorderAdapter))
            itemTouchHelper?.attachToRecyclerView(reorderRecyclerView)
            reorderRecyclerView.adapter = reorderAdapter

            reorderSaveLayout.positiveButton.text = getString(R.string.save_changes)
            reorderSaveLayout.positiveButton.clicks {
                viewModel.saveOrder()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.reorderSaveLayout.oneButtonLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.itemList.subscribe {
                reorderAdapter?.updateData(it)
            },
            viewModel.reorderResult.subscribe {
                listener?.getReorderResult(it)
                goBack()
            }
        )

        arguments?.getStringArrayList(ITEM_LIST)?.toList()?.let {
            viewModel.loadData(ReorderParam(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reorderAdapter = null
    }

    companion object {
        private const val ITEM_LIST = "itemList"

        @JvmStatic
        fun newInstance(itemList: List<String>, listener: ReorderListener) =
            ReorderFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ITEM_LIST, ArrayList(itemList))
                }
                this.listener = listener
            }
    }

    interface ReorderListener {
        fun getReorderResult(reorderResult: List<String>)
    }
}