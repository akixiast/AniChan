package com.aki.anichan.ui.reorder

import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

interface DragListener {
    fun onStartDrag(viewHolder: BaseRecyclerViewAdapter<*, *>.ViewHolder)
}