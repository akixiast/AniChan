package com.aki.anichan.ui.reorder

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

abstract class BaseReorderRecyclerViewAdapter<T, VB: ViewBinding>(
    list: List<T>
) : BaseRecyclerViewAdapter<T, VB>(list), ItemMoveListener